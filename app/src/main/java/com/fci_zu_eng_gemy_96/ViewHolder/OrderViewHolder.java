package com.fci_zu_eng_gemy_96.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Interface.ItemClickListener;
import com.fci_zu_eng_gemy_96.R;

public class OrderViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener , View.OnClickListener {

    public TextView txtOrderId , txtOrderStatus , txtOrderPhone , txtOrderAddress ;
    public ItemClickListener itemClickListener ;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_adress);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }


    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
