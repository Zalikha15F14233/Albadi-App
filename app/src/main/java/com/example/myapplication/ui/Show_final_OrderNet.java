package com.example.myapplication.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.Permeation;
import com.example.myapplication.InterFace.itemOnClickListener;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.Finailorder_fromnet_ViewHolder;
import com.example.myapplication.model.DataOrderLocal;
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

import java.io.File;
import java.util.ArrayList;

public class Show_final_OrderNet extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {




    //@@@@@@@@@@@@  in this must be use recycal with heder and futter becuse must be print in last list  @@@@@
    /// to crtaet pdf

    private static final String TAG = "PdfCreatorActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    ImageView imgdownload;
    // ConnectionClass connectionClass;
    ArrayList<DataOrderLocal> MyList1;
    DataOrderLocal giftitemPOJO;
    Context context;
    DataOrderLocal name;
    DataOrderLocal price;
    //    DataOrderLocal url;
//    DataOrderLocal type;
    DataOrderLocal date;
//    DataOrderLocal idorder_class;
//    DataOrderLocal location_class;

    /// end to crete pdf


    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    String idorder_string,location_string,total_string;
    TextView idorder,location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_final_order_net);

        context = this;
        giftitemPOJO = new DataOrderLocal();

        Bundle extras = getIntent().getExtras(); // to get move intent
        if (extras != null) {
            String a = extras.getString("id");
            String locations = extras.getString("location");
            String total = extras.getString("total");
            if (a!=null){
                idorder_string= a;
                location_string=locations;
                total_string=total;
//                Toast.makeText(this, "???"+aa, Toast.LENGTH_SHORT).show();
            }
        }

        Permeation permeation=new Permeation();
        permeation.checkAndRequestPermissions(Show_final_OrderNet.this);

        idorder=findViewById(R.id.text_idorder_finalorder);
        idorder.setText("id order : "+idorder_string);
        location=findViewById(R.id.text_location_finalorder);
        location.setText("location : "+location_string);




        recyclerView=findViewById(R.id.recycal_home);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Request");
        //databaseReference.keepSynced(true);//to catch data if net offline

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        }
        else{
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(Show_final_OrderNet.this);
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
                    Toast.makeText(Show_final_OrderNet.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onRefresh() {
        startUI();
    }

    private FirebaseRecyclerAdapter<DataOrderLocal, Finailorder_fromnet_ViewHolder> firebaseRecyclerAdapter;
    private void startUI() {

        MyList1 = new ArrayList<DataOrderLocal>();
//
//        FirebaseRecyclerAdapter<DataOrderLocal, Finailorder_fromnet_ViewHolder> firebaseRecyclerAdapters=new FirebaseRecyclerAdapter<DataOrderLocal, Finailorder_fromnet_ViewHolder>(
//                DataOrderLocal.class,R.layout.show_finalorder_fromnet, Finailorder_fromnet_ViewHolder.class,databaseReference.child(idorder_string).child("food")
//        ) {
//            @Override
//            protected void populateViewHolder(final Finailorder_fromnet_ViewHolder viewHolder, final DataOrderLocal model, final int position) {
//
//                final String postke=getRef(position).getKey();
//
//                databaseReference.child(postke).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(final DataSnapshot dataSnapshot) {
//                        //Toast.makeText(context, ">>"+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//                        //to create pdf to fatora
//                        giftitemPOJO = new DataOrderLocal();
//                        // while (rs.next()) {
////                        giftitemPOJO.setImageurl(model.getImageurl());
////                        giftitemPOJO.setCount(model.getCount());
////                        giftitemPOJO.setId(model.getId());
//                        giftitemPOJO.setProdectamount(model.getProdectamount());
////                        giftitemPOJO.setProdectDiscount(model.getProdectDiscount());
////                        giftitemPOJO.setProdectid(model.getProdectid());
//                        giftitemPOJO.setProdectname(model.getProdectname());
//                        giftitemPOJO.setProdectPrice(model.getProdectPrice());
////                        giftitemPOJO.setTotalprice(model.getTotalprice());
//
//                        MyList1.add(giftitemPOJO);
//
//                        String postkesaa=dataSnapshot.getRef().getKey();
//                        Log.e("json","json: _________>" + postkesaa);
//
//                        mSwipeRefreshLayout.setRefreshing(false);
//
//                        viewHolder.price.setText("price : "+model.getProdectPrice());
//                        viewHolder.numfood.setText("amount food : "+model.getProdectamount());
//                        viewHolder.namefood.setText("name food : "+model.getProdectname());
////                        Picasso.with(getBaseContext()).load(model.getImageurl()).into(viewHolder.images);
//                        Picasso.get().load(model.getImageurl()).into(viewHolder.images);
//
//
//                        viewHolder.setItemOnClickListener(new itemOnClickListener() {
//                            @Override
//                            public void onClick(View view, int position, boolean isLongClick) {
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        };
//        recyclerView.setAdapter(firebaseRecyclerAdapters);
//
//        if(firebaseRecyclerAdapters.getItemCount()==0){ //this is always returning 0 even when my recyclerview has values
//            mSwipeRefreshLayout.setRefreshing(false);
//        }




        FirebaseRecyclerOptions<DataOrderLocal> options =
                new FirebaseRecyclerOptions.Builder<DataOrderLocal>()
                        .setQuery(databaseReference.child(idorder_string).child("food"), DataOrderLocal.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DataOrderLocal, Finailorder_fromnet_ViewHolder>(options) {
                    @NonNull
                    @Override
                    public Finailorder_fromnet_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_finalorder_fromnet, parent, false);
                        return new Finailorder_fromnet_ViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull Finailorder_fromnet_ViewHolder viewHolder, int position, DataOrderLocal model) {
                        final String postke=getRef(position).getKey();

                        databaseReference.child(postke).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                //Toast.makeText(context, ">>"+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                //to create pdf to fatora
                                giftitemPOJO = new DataOrderLocal();
                                // while (rs.next()) {
//                        giftitemPOJO.setImageurl(model.getImageurl());
//                        giftitemPOJO.setCount(model.getCount());
//                        giftitemPOJO.setId(model.getId());
                                giftitemPOJO.setProdectamount(model.getProdectamount());
//                        giftitemPOJO.setProdectDiscount(model.getProdectDiscount());
//                        giftitemPOJO.setProdectid(model.getProdectid());
                                giftitemPOJO.setProdectname(model.getProdectname());
                                giftitemPOJO.setProdectPrice(model.getProdectPrice());
//                        giftitemPOJO.setTotalprice(model.getTotalprice());

                                MyList1.add(giftitemPOJO);

                                String postkesaa=dataSnapshot.getRef().getKey();
                                Log.e("json","json: _________>" + postkesaa);

                                mSwipeRefreshLayout.setRefreshing(false);

                                viewHolder.price.setText("price : "+model.getProdectPrice());
                                viewHolder.numfood.setText("amount food : "+model.getProdectamount());
                                viewHolder.namefood.setText("name food : "+model.getProdectname());
//                        Picasso.with(getBaseContext()).load(model.getImageurl()).into(viewHolder.images);
                                Picasso.get().load(model.getImageurl()).into(viewHolder.images);


                                viewHolder.setItemOnClickListener(new itemOnClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mSwipeRefreshLayout.setRefreshing(false);
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
