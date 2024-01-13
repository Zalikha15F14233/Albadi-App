package com.example.myapplication.ViewHolder;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;


public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderid,status,phone,addrese;
    public ImageView images;
    public Button showorder,deleteorder,payment,chat;
   com.example.myapplication.InterFace.itemOnClickListener itemOnClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);


        orderid=(TextView) itemView.findViewById(R.id.text_orderid_itemcart);
        status= (TextView) itemView.findViewById(R.id.text_status_itemcart);
        phone= (TextView) itemView.findViewById(R.id.text_orderphone_itemcart);
        addrese= (TextView) itemView.findViewById(R.id.text_address_itemcart);
        showorder= (Button) itemView.findViewById(R.id.bu_showorder_itemordersattus);
        deleteorder= (Button) itemView.findViewById(R.id.bu_deleatorder_itemordersattus);
        payment= (Button) itemView.findViewById(R.id.bu_payment_itemordersattus);
        chat= (Button) itemView.findViewById(R.id.bu_chat_itemordersattus);

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
