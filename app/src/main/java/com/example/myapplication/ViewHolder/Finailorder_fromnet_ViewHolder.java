package com.example.myapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class Finailorder_fromnet_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView namefood,numfood,price,text_des;
    public ImageView images,imageprint;

    com.example.myapplication.InterFace.itemOnClickListener itemOnClickListener;

    public Finailorder_fromnet_ViewHolder(@NonNull View itemView) {
        super(itemView);


        namefood=(TextView) itemView.findViewById(R.id.text_namefood_showfinalorderfromnet);
        numfood=(TextView) itemView.findViewById(R.id.text_numfood_showfinalorderfromnet);
        price=(TextView) itemView.findViewById(R.id.text_price_showfinalorderfromnet);
        text_des=(TextView) itemView.findViewById(R.id.text_des);

        images= (ImageView) itemView.findViewById(R.id.image_showfinalorderfromnet);
        imageprint= (ImageView) itemView.findViewById(R.id.image_print_finalorder_fromnet);



        itemView.setOnClickListener(this);
    }

    public void setItemOnClickListener(com.example.myapplication.InterFace.itemOnClickListener itemOnClickListener) {

        this.itemOnClickListener = itemOnClickListener;
    }

    @Override
    public void onClick(View view) {
        itemOnClickListener.onClick(view,getAdapterPosition(),false);

    }
}
