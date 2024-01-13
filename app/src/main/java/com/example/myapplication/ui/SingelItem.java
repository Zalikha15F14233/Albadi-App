package com.example.myapplication.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.Food_singelViewHolder;
import com.example.myapplication.model.DataProduct;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingelItem extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {


    String idCat;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singel_item);


        Bundle extras = getIntent().getExtras(); // to get move intent
        if (extras != null) {
            String a = extras.getString("id");
            if (a!=null){
                idCat= a;
            }

        }

        recyclerView=findViewById(R.id.recycal_home1);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");
        //databaseReference.keepSynced(true);//to catch data if net offline

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container1);
        mSwipeRefreshLayout.setOnRefreshListener(SingelItem.this);
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
                }else{
                    Toast.makeText(SingelItem.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onRefresh() {
        startUI();
    }

    private FirebaseRecyclerAdapter<DataProduct, Food_singelViewHolder> firebaseRecyclerAdapter;

    private void startUI() {
        FirebaseRecyclerOptions<DataProduct> options =
                new FirebaseRecyclerOptions.Builder<DataProduct>()
                        .setQuery(databaseReference.orderByChild("menuId").equalTo(idCat), DataProduct.class)
                        .build();


//        FirebaseRecyclerAdapter<Data_Category, MenuViewHolder>
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DataProduct, Food_singelViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull Food_singelViewHolder viewHolder, int position, DataProduct model) {

                        final String postKey = getRef(position).getKey();

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Toast.makeText(Home.this, "!!!!"+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();

                                mSwipeRefreshLayout.setRefreshing(false);
                                viewHolder.posttext.setText(model.getName());
                                Picasso.get().load(model.getImage()).into(viewHolder.images);

                                viewHolder.setItemOnClickListener((view, position1, isLongClick) -> {
                                    Toast.makeText(SingelItem.this, "" + model.getName(), Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                // calling on cancelled method when we receive
                                // any error or we are not able to get the data.
                                Toast.makeText(SingelItem.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                            }
                        });

                       }

                    @NonNull
                    @Override
                    public Food_singelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
                        return new Food_singelViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        mSwipeRefreshLayout.setRefreshing(false);


    }
}