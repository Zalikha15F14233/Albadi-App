package com.example.myapplication.ui;

import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.InterFace.itemOnClickListener;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.Favorite_ViewHolder;
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

public class Favorites extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        recyclerView = findViewById(R.id.recycal_home);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite");
        //databaseReference.keepSynced(true);//to catch data if net offline

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(Favorites.this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    mSwipeRefreshLayout.setRefreshing(true);
                    startUI();
                } else {
                    Toast.makeText(Favorites.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public void onRefresh() {
        startUI();
    }

    private FirebaseRecyclerAdapter<Favorite_Data, Favorite_ViewHolder> firebaseRecyclerAdapter;

    private void startUI() {

        FirebaseRecyclerOptions<Favorite_Data> options =
                new FirebaseRecyclerOptions.Builder<Favorite_Data>()
                        .setQuery(databaseReference.child(firebaseUser.getUid()), Favorite_Data.class)
                        .build();


//        FirebaseRecyclerAdapter<Data_Category, MenuViewHolder>
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Favorite_Data, Favorite_ViewHolder>(options) {
                    @NonNull
                    @Override
                    public Favorite_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singel_product, parent, false);
                        return new Favorite_ViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull Favorite_ViewHolder viewHolder, int position, Favorite_Data model) {
//                        String postkesaa = dataSnapshot.getRef().getKey();
//                        Log.e("json", "json: _________>" + postkesaa);

                        mSwipeRefreshLayout.setRefreshing(false);
                        viewHolder.imageFanorit.setImageResource(android.R.drawable.star_big_on);

                        final DatabaseReference databaseReference_favorit = FirebaseDatabase.getInstance().getReference().child("Products");
                        //databaseReference_favorit .child(firebaseUser.getUid());
                        //  databaseReference_favorit .child("Favorite_Data");
                        databaseReference_favorit.child(model.getId_item()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshots) {
                                String postkes = dataSnapshots.getKey();

                                Object object = dataSnapshots.getValue(Object.class);
                                String json = new Gson().toJson(object);
                                Data_Favorite example = new Gson().fromJson(json, Data_Favorite.class);
                                Log.e("json", "@@@json: _________>" + json);

                                viewHolder.posttext.setText(
                                        dataSnapshots.child("name").getValue().toString()
                                );
//                                Picasso.with(getBaseContext()).load(dataSnapshots.child("image").getValue().toString()).into(viewHolder.images);
                                Picasso.get().load(dataSnapshots.child("image").getValue().toString()).into(viewHolder.images);

                                viewHolder.setItemOnClickListener(new itemOnClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        // Toast.makeText(SingelItem.this, ""+model.getName(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
                                        intent.putExtra("id", getRef(position).getKey());
                                        //intent.putExtra("true",);
                                        startActivity(intent);
                                    }
                                });

                                for (DataSnapshot postSnanpHOt : dataSnapshots.getChildren()) {
                                    //DataFood data_favoriteData = postSnanpHOt.getValue(DataFood.class);
                                }
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