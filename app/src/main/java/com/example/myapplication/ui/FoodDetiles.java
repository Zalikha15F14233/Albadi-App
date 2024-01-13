package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database_local.Database_order_local;
import com.example.myapplication.model.DataFood;
import com.example.myapplication.model.Favorite_Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
public class FoodDetiles extends AppCompatActivity {


    TextView foodname,foodmony,description,discount;
//    ElegantNumberButton elegantNumberButton;
    ImageView foodimage;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String idCat;

    CollapsingToolbarLayout collapsingToolbarLayout;

    DataFood dataFood;

    FloatingActionButton fab,fab_favoreat;

    Database_order_local database_allnotes;

    String favorite_chek=null;



    LinearLayout linearLayout_discount;

    String image_url;
    TextView num;
    int numFinal=1;
    ImageButton addBu,minimizeBu;
    //databaseReference.child(key).removeValue();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detiles);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab_favoreat = (FloatingActionButton) findViewById(R.id.fab_favoreat);


        fab_favoreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_topish=String.valueOf(System.currentTimeMillis());

//                Log.e("bu",favorite_chek);
                if (favorite_chek==null){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorite").child(firebaseUser.getUid());
                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("id_item", idCat);
                    usermap.put("id_user", firebaseUser.getUid());
                    usermap.put("id_topish", idCat);
                    databaseReference.child(idCat).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                fab_favoreat.setImageResource(R.drawable.baseline_star_24);
//                                fab_favoreat.setImageResource(android.R.drawable.star_big_on);
                            } else {
                            }
                        }
                    });
                    Toast.makeText(FoodDetiles.this, "Add Favorite_Data", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Favorite").child(firebaseUser.getUid()).child(idCat);
//                    dR.removeValue();
                    dR. removeValue();
                    fab_favoreat.setImageResource(R.drawable.baseline_star_outline);
                    favorite_chek=null;
                    Toast.makeText(FoodDetiles.this, "You are remove this item from favorite !!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        Bundle extras = getIntent().getExtras(); // to get move intent
        if (extras != null) {
            String a = extras.getString("id");
            String aa = extras.getString("true");
            if (a!=null){
                idCat= a;
//                Toast.makeText(this, "???"+aa, Toast.LENGTH_SHORT).show();
            }
        }




        databaseReference= FirebaseDatabase.getInstance().getReference().child("Foods");

        collapsingToolbarLayout=findViewById(R.id.toolbar_layout);
        addBu=findViewById(R.id.add);
        num=findViewById(R.id.num);
        addBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numFinal=numFinal+1;
                num.setText(""+numFinal);
            }
        });
        minimizeBu=findViewById(R.id.minimize);
        minimizeBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numFinal>1){
                    numFinal=numFinal-1;
                    num.setText(""+numFinal);
                }

            }
        });


        foodimage=findViewById(R.id.foodimage_fooddetels);
        foodmony=findViewById(R.id.text_money_fooddetelies);
        foodname=findViewById(R.id.text_foodname_fooddetelies);
        description=findViewById(R.id.text_descrebtion_fooddetelies);
//        elegantNumberButton=findViewById(R.id.ElegantNumberButton_fooddescription);

        discount=findViewById(R.id.text_discount_fooddetelies);
        linearLayout_discount=findViewById(R.id.liner_decound);

        cek_button();// to check if add in cart or not to show button or disable


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                database_allnotes=new Database_order_local(FoodDetiles.this);
                database_allnotes.open();
                //String name,String id,String amunt,String price,String discription
                database_allnotes.insert(
                        dataFood.getName(),idCat, String.valueOf(numFinal),
                        dataFood.getPrice(),dataFood.getDiscount(),image_url
                );

                cek_button();
                Toast.makeText(FoodDetiles.this, "Add Cart", Toast.LENGTH_LONG).show();

//                Snackbar.make(view, "Replace  "+elegantNumberButton.getNumber(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });


        getdetiles();
    }

    @SuppressLint("RestrictedApi")
    private void cek_button() {
        database_allnotes=new Database_order_local(FoodDetiles.this);
        database_allnotes.open();
        if (database_allnotes.cek_isExist_orNot(idCat) == true) {
            fab.setVisibility(View.GONE);
//            cek_button();

        }else{
            fab.setVisibility(View.VISIBLE);
        }
    }


    private void getdetiles() {
//        databaseReference.child(idCat)
        databaseReference.child(idCat)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                    dataFood=dataSnapshot.getValue(DataFood.class);

                    Picasso.get().load(dataFood.getImage()).into(foodimage);

                    image_url=dataFood.getImage();
                    foodmony.setText(dataFood.getPrice());
                    foodname.setText(dataFood.getName());
                    description.setText(dataFood.getDescription());
                    collapsingToolbarLayout.setTitle(dataFood.getName());

                    if (dataFood.getDescription().equals("")){
                        linearLayout_discount.setVisibility(View.GONE);
                    }else{
                        discount.setText(dataFood.getDiscount());
                    }

                }else{
                    Toast.makeText(FoodDetiles.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        final DatabaseReference databaseReference_favorit= FirebaseDatabase.getInstance().getReference().child("Favorite");
//        databaseReference_favorit.child(firebaseUser.getUid());
        //  databaseReference_favorit .child("Favorite_Data");
        databaseReference_favorit.child(firebaseUser.getUid()).orderByChild("id_item").equalTo(idCat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshots) {
//                String postkes=dataSnapshots.getKey();
//
//                Object object = dataSnapshots.getValue(Object.class);
//                String json = new Gson().toJson(object);
//                Data_Favorite example= new Gson().fromJson(json, Data_Favorite.class);
//                Log.e("json","@@@json: _________>" + json);



                for (DataSnapshot postSnanpHOt : dataSnapshots.getChildren()) {
                    final Favorite_Data data_favoriteData = postSnanpHOt.getValue(Favorite_Data.class);
                    if (data_favoriteData.getId_item()!=null){
                        fab_favoreat.setImageResource(R.drawable.baseline_star_24);
                        favorite_chek= data_favoriteData.getId_item();
                        Log.e("json","@@@json: _________>" + favorite_chek);
                    }else{
                        fab_favoreat.setImageResource(R.drawable.baseline_star_outline);
                        favorite_chek= data_favoriteData.getId_item();
                        Log.e("json","@@@json: _________>" + favorite_chek);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
