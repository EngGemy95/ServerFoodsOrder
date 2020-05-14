package com.fci_zu_eng_gemy_96;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Interface.ItemClickListener;
import com.fci_zu_eng_gemy_96.Model.Requests;
import com.fci_zu_eng_gemy_96.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;

    FirebaseDatabase database ;
    DatabaseReference requestRef ;
    FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter ;
    FirebaseRecyclerOptions<Requests> options;

    MaterialSpinner spinner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requestRef = database.getReference("requests");
        requestRef.keepSynced(true);

        //init
        recyclerView = findViewById(R.id.recycler_order_status);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(); // load all orders

    }

    private void loadOrders() {
        options = new FirebaseRecyclerOptions.Builder<Requests>().setQuery(requestRef,Requests.class).build();

        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Requests model) {

                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderAddress.setText(model.getAddress());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                     public void onClick(View view, int position, boolean isLongClick) {
                        Intent trackingOrder = new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest = model ;
                        startActivity(trackingOrder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_status_layout,parent,false);
                return new OrderViewHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){
            showDeletDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void showDeletDialog(String key) {
        requestRef.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Requests item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Order");
        builder.setMessage("Please Choose Status");

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = view.findViewById(R.id.StatusSpinner);
        spinner.setItems("Placed","On My Way","Delivered");

        builder.setView(view);

        final String localKey = key ;

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requestRef.child(localKey).setValue(item);
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
}
