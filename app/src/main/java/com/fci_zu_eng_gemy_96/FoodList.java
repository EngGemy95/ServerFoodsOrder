package com.fci_zu_eng_gemy_96;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Interface.ItemClickListener;
import com.fci_zu_eng_gemy_96.Model.Category;
import com.fci_zu_eng_gemy_96.Model.Food;
import com.fci_zu_eng_gemy_96.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodRef;
    FirebaseRecyclerOptions<Food> options;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseStorage storage;
    StorageReference storageRef;

    FloatingActionButton fab;
    String CategoryId = "";
    MaterialEditText edtFoodName, edtFoodDescription, edtFoodPrice, edtFoodDiscount;
    FButton btnSelect, btnUpload;
    final static int REQUEST_CODE = 55;
    Uri saveImageUri;
    Food food;

    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        database = FirebaseDatabase.getInstance();
        foodRef = database.getReference("Foods");
        foodRef.keepSynced(true);

        rootLayout = findViewById(R.id.rootLayout);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        recyclerView = findViewById(R.id.recyclerview_food);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFoodDialog();
            }
        });

        if (getIntent() != null) {
            CategoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!CategoryId.isEmpty()) {
            loadFoods(CategoryId);
        }

    }

    private void addNewFoodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Fill All information");
        builder.setTitle("Add New Food");
        builder.setIcon(R.drawable.ic_playlist_add_black_24dp);

        LayoutInflater inflater = getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_new_food_layout, null);
        edtFoodName = add_food_layout.findViewById(R.id.edtFoodName);
        edtFoodDescription = add_food_layout.findViewById(R.id.edtFoodDescription);
        edtFoodPrice = add_food_layout.findViewById(R.id.edtFoodPrice);
        edtFoodDiscount = add_food_layout.findViewById(R.id.edtFoodDiscount);

        btnSelect = add_food_layout.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnUpload = add_food_layout.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        builder.setView(add_food_layout);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (food != null) {
                    foodRef.push().setValue(food);
                    Snackbar.make(rootLayout, "new food " + food.getName() + " added Successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void uploadImage() {
        if (saveImageUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("images/" + imageName);
            imageFolder.putFile(saveImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, R.string.uploaded, Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    food = new Food(edtFoodName.getText().toString()
                                            , edtFoodPrice.getText().toString()
                                            , edtFoodDiscount.getText().toString()
                                            , uri.toString()
                                            , edtFoodDescription.getText().toString()
                                            , CategoryId);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            float progress = 100 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded " + progress + " %");
                        }
                    });
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveImageUri = null;
            saveImageUri = data.getData();
            btnSelect.setText(R.string.image_selected);
        }
    }

    private void loadFoods(String menuId) {
        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodRef.orderByChild("menuId").equalTo(menuId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.foodImage);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        if (item.getTitle().equals(Common.DELETE)) {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodRef.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Fill All information");
        builder.setTitle("Edit Food");
        builder.setIcon(R.drawable.ic_playlist_add_black_24dp);

        LayoutInflater inflater = getLayoutInflater();
        View add_food_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtFoodName = add_food_layout.findViewById(R.id.edtFoodName);
        edtFoodDescription = add_food_layout.findViewById(R.id.edtFoodDescription);
        edtFoodPrice = add_food_layout.findViewById(R.id.edtFoodPrice);
        edtFoodDiscount = add_food_layout.findViewById(R.id.edtFoodDiscount);

        edtFoodName.setText(item.getName());
        edtFoodDescription.setText(item.getDescription());
        edtFoodPrice.setText(item.getPrice());
        edtFoodDiscount.setText(item.getDiscount());

        btnSelect = add_food_layout.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnUpload = add_food_layout.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeImage(item);
            }
        });

        builder.setView(add_food_layout);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if (item != null) {
                    item.setName(edtFoodName.getText().toString());
                    item.setDescription(edtFoodDescription.getText().toString());
                    item.setDiscount(edtFoodDiscount.getText().toString());
                    item.setPrice(edtFoodPrice.getText().toString());

                    foodRef.child(key).setValue(item);
                    Snackbar.make(rootLayout, "The food " + item.getName() + " edited Successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void changeImage(final Food item) {
        if (saveImageUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageRef.child("images/" + imageName);
            imageFolder.putFile(saveImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, R.string.uploaded, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            //String progressNumber = String.format("%02",progress);
                            dialog.setMessage("Uploaded " + progress + " %");
                        }
                    });
        }
    }
}
