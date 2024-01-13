package com.example.myapplication.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;


public class SupCategory_ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener , View.OnCreateContextMenuListener {


    public TextView discription,name,price,discount;
    public ImageView images;
    public ImageButton deleat_banner;

    com.example.myapplication.InterFace.itemOnClickListener itemOnClickListener;

    public SupCategory_ViewHolder(@NonNull View itemView) {
        super(itemView);


        discription =(TextView) itemView.findViewById(R.id.text_description_supcategry);
        name =(TextView) itemView.findViewById(R.id.text_name_supcategry);
        price =(TextView) itemView.findViewById(R.id.text_price_supcategry);
        discount =(TextView) itemView.findViewById(R.id.text_discount_supcategry);

        images= (ImageView) itemView.findViewById(R.id.image_supcategry);
        deleat_banner= (ImageButton) itemView.findViewById(R.id.imagebu_deleet_supcategry);

        itemView.setOnCreateContextMenuListener(this);

        itemView.setOnClickListener(this);
    }

    public void setItemOnClickListener(com.example.myapplication.InterFace.itemOnClickListener itemOnClickListener) {

        this.itemOnClickListener = itemOnClickListener;
    }

    @Override
    public void onClick(View view) {
        itemOnClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.add(0,0,getAdapterPosition(),"UpDate");
        contextMenu.add(0,1,getAdapterPosition(),"Add to Banner");
    }


}

