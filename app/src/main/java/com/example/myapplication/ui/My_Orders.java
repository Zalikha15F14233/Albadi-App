package com.example.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.InterFace.itemOnClickListener;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.Favorite_ViewHolder;
import com.example.myapplication.ViewHolder.OrderViewHolder;
import com.example.myapplication.model.Chat;
import com.example.myapplication.model.DataReqeste;
import com.example.myapplication.model.Data_Favorite;
import com.example.myapplication.model.Favorite_Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


public class My_Orders extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{



    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
//        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recycal_home);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
        //databaseReference.keepSynced(true);//to catch data if net offline

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    mSwipeRefreshLayout.setRefreshing(true);
                    startUI();
                } else {
                    Toast.makeText(getApplicationContext(), "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public void onRefresh() {
        startUI();
    }

    private FirebaseRecyclerAdapter<DataReqeste, OrderViewHolder> firebaseRecyclerAdapter;

    private void startUI() {
        FirebaseRecyclerOptions<DataReqeste> options =
                new FirebaseRecyclerOptions.Builder<DataReqeste>()
                        .setQuery(databaseReference.orderByChild("iduser").equalTo(firebaseUser.getUid()), DataReqeste.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DataReqeste, OrderViewHolder>(options) {
                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderstatus, parent, false);
                        return new OrderViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, DataReqeste model) {

                        final String postke = getRef(position).getKey();
                        databaseReference.child(postke).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                mSwipeRefreshLayout.setRefreshing(false);
                                ;

                                // get massage
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
                                reference.orderByChild("idorder").equalTo(postke).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int unread = 0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            Chat chat = snapshot.getValue(Chat.class);
                                            if (chat.getReceiver().equals("user")
                                                    && !chat.isIsseen()){
                                                unread++;
                                            }
                                        }

                                        if (unread == 0){
                                            try {
                                                viewHolder.orderid.setText(getRef(position).getKey());
                                            }catch (Exception e){

                                            }
                                        } else {
                                            try {
                                            viewHolder.orderid.setText(getRef(position).getKey()+"\n"+"(" + unread + ") Massage");
                                        }catch (Exception e){

                                        }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                viewHolder.addrese.setText(model.getAddress());
                                viewHolder.status.setText(convertcode(model.getStatus()));

                                DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("User");
                                databaseReferenceUser.child(model.getIduser()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        mSwipeRefreshLayout.setRefreshing(false);
                                        final String phoneuser = dataSnapshot.child("phone").getValue().toString();
                                        viewHolder.phone.setText(phoneuser);



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                });

                                if (model.getStatus().equals("0") || model.getStatus().equals("1")){
                                    viewHolder.chat.setVisibility(View.GONE);
                                }

                                viewHolder.chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });


                                viewHolder.payment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                });

//                                Picasso.get().load(model.getFood().get(position).getImageurl()).into(viewHolder.images);

                                viewHolder.showorder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                Intent intent=new Intent(getApplicationContext(), Show_final_OrderNet.class);
                                intent.putExtra("id",getRef(position).getKey());
                                intent.putExtra("location",model.getAddress());
                                intent.putExtra("total",model.getTotal());
                                startActivity(intent);
                                    }
                                });

                                if (model.getStatus().equals("3")){
                                    viewHolder.deleteorder.setVisibility(View.GONE);
                                    viewHolder.payment.setVisibility(View.GONE);
                                }
                                viewHolder.deleteorder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        final AlertDialog.Builder builder=new AlertDialog.Builder(My_Orders.this);
                                        builder.setTitle("Do you want delete !");
                                        builder.setMessage("Do you want delete this item ");

                                        builder.setIcon(R.drawable.baseline_drive_file_rename_outline_24);
                                        builder.setPositiveButton("Delete ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                                                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
                                                    databaseReference.child(postke).removeValue();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Check Your Network", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        builder.setNegativeButton("no ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }


                            private String convertcode(String status) {

                                if (status.equals("0"))
                                    return "Placed";
                                else if (status.equals("1"))
                                    return "Working On";
                                else if (status.equals("2"))
                                    return "On My Way";
                                else
                                    return "Shipped";
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

//                    @NonNull
//                    @Override
//                    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singel_food, parent, false);
//                        return new MenuViewHolder(view);
//                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}