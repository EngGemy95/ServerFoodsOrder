package com.fci_zu_eng_gemy_96.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Interface.ItemClickListener;
import com.fci_zu_eng_gemy_96.R;

public class FoodViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView foodName ;
    public ImageView foodImage ;
    public ItemClickListener itemClickListener ;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName = itemView.findViewById(R.id.name_of_food);
        foodImage = itemView.findViewById(R.id.image_of_food);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
