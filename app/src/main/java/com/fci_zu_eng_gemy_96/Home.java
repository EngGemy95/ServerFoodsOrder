package com.fci_zu_eng_gemy_96;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Interface.ItemClickListener;
import com.fci_zu_eng_gemy_96.Model.Category;
import com.fci_zu_eng_gemy_96.Service.ListenOrder;
import com.fci_zu_eng_gemy_96.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    TextView txtFullName;
    MaterialEditText edtMenuName;
    FButton btnSelect, btnUpload;
    Uri saveImageUri;

    FirebaseDatabase database;
    DatabaseReference CategoryRef;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseRecyclerOptions<Category> options;

    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView menuRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    Category newCategory;
    private final int PICK_IMAGE_REQUEST = 22;

    DrawerLayout drawer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.menu);
        setSupportActionBar(toolbar);


        database = FirebaseDatabase.getInstance();
        CategoryRef = database.getReference("Category");
        CategoryRef.keepSynced(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        menuRecyclerView = findViewById(R.id.recycler_menu);
        menuRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                showDialog();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();*/

        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtUserFullName);
        txtFullName.setText(Common.currentUser.getName());

        loadMenu();

        Intent service = new Intent(this, ListenOrder.class);
        startService(service);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Category");
        builder.setMessage("Please fill Full information");

        LayoutInflater inflater = getLayoutInflater();
        View add_mennu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        edtMenuName = add_mennu_layout.findViewById(R.id.edtMenuName);
        btnSelect = add_mennu_layout.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload = add_mennu_layout.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        builder.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
        builder.setView(add_mennu_layout);

        //if user enter Yes Button
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // here we create new Category
               if (newCategory !=null){
                   CategoryRef.push().setValue(newCategory);
                   Snackbar.make(drawer,"new Category "+newCategory.getName()+" added Successfully "
                           ,Snackbar.LENGTH_SHORT).show();
               }
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void uploadImage() {
        if (saveImageUri !=null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, R.string.uploaded, Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            newCategory = new Category(edtMenuName.getText().toString(),uri.toString());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            float progress =  100 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //String progressNumber = String.format("%02",progress);
                            dialog.setMessage("Uploaded "+progress+" %");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            saveImageUri = null;
            saveImageUri = data.getData();
            btnSelect.setText(R.string.image_selected);
        }
    }

    private void loadMenu() {
        options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(CategoryRef, Category.class).build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final MenuViewHolder holder, int position, Category model) {
                holder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(Home.this,FoodList.class);
                        intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        menuRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_menu) {
            startActivity(new Intent(this, Home.class));
        } else if (id == R.id.nav_cart) {
            //startActivity(new Intent(this,Cart.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this,OrderStatus.class));
        } else if (id == R.id.nav_sign_out) {
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpadteDialog(adapter.getRef(item.getOrder()).getKey() , adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        DatabaseReference databaseReference = database.getReference("Foods");
        Query foodInCategory = databaseReference.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()){
                    post.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        CategoryRef.child(key).removeValue();
        Toast.makeText(this, "Item Deleted !!", Toast.LENGTH_SHORT).show();
    }

    private void showUpadteDialog(final String key, final Category item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Category");
        builder.setMessage("Please fill All information");

        LayoutInflater inflater = getLayoutInflater();
        View add_mennu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        edtMenuName = add_mennu_layout.findViewById(R.id.edtMenuName);
        btnSelect = add_mennu_layout.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload = add_mennu_layout.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        builder.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
        builder.setView(add_mennu_layout);

        //if user enter Yes Button
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // here we create new Category
                item.setName(edtMenuName.getText().toString());
                CategoryRef.child(key).setValue(item);
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void changeImage(final Category item) {
        if (saveImageUri !=null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, R.string.uploaded, Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //newCategory = new Category(edtMenuName.getText().toString(),uri.toString());
                                            item.setImage(uri.toString());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =  (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            //String progressNumber = String.format("%02",progress);
                            dialog.setMessage("Uploaded "+progress+" %");
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    /*    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
}
