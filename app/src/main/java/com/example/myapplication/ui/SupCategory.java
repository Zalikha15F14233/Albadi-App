package com.example.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.InterFace.itemOnClickListener;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.SupCategory_ViewHolder;
import com.example.myapplication.model.Data_SupCategrory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class SupCategory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    SwipeRefreshLayout mSwipeRefreshLayout;

    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseRecyclerAdapter<Data_SupCategrory, SupCategory_ViewHolder> adabtor;

    String idCat;
    EditText quantity,name, discrption,price,descount;
    Button bu_select;

    Uri saveUri=null;
    ProgressDialog mDialog;
    int Pick_Image=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sup_category);


        Bundle extras = getIntent().getExtras(); // to get move intent
        if (extras != null) {
            String a = extras.getString("id");
            if (a!=null){
                idCat= a;
            }
        }

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recyclerView=findViewById(R.id.recycal_home);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Products");
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
        mSwipeRefreshLayout.setOnRefreshListener(SupCategory.this);
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
                    Toast.makeText(SupCategory.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });


        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(SupCategory.this);
                builder.setTitle("One More Step!");
                builder.setMessage("Enter Your Product");

                LayoutInflater inflater=SupCategory.this.getLayoutInflater();
                View add_menu=inflater.inflate(R.layout.add_product,null);

                name=add_menu.findViewById(R.id.ed_nmaefood_home);
                quantity=add_menu.findViewById(R.id.ed_quantity_home);
                discrption =add_menu.findViewById(R.id.ed_decriptionfood_home);
                price=add_menu.findViewById(R.id.ed_pricefood_home);
                descount=add_menu.findViewById(R.id.ed_nmaemenu_home);

                bu_select=add_menu.findViewById(R.id.bu_selectimage_home);
                bu_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        show_Image();
                    }
                });

//                edtUplode=add_menu.findViewById(R.id.ed_uploade_home);
//                edtUplode.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                });

                builder.setView(add_menu);
                builder.setIcon(R.drawable.ic_shopping);
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (name.getText().toString().isEmpty()&&price.getText().toString().isEmpty()){
                            name.setError("Enter Name Product !!");
                            name.setFocusable(true);

                        }else {
                            upoladeImage(name.getText().toString().trim(),
                                    discrption.getText().toString().trim(),
                                    price.getText().toString().trim(),
                                    descount.getText().toString().trim(),
                                    quantity.getText().toString().trim(),
                                    dialogInterface
                            );
                        }
                        // dialogInterface.dismiss();
                    }


                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }


                });

                builder.show();
            }
        });


    }

    @Override
    public void onRefresh() {
        startUI();
    }


    private void startUI() {
        FirebaseRecyclerOptions<Data_SupCategrory> options =
                new FirebaseRecyclerOptions.Builder<Data_SupCategrory>()
                        .setQuery(databaseReference.orderByChild("menuId").equalTo(idCat), Data_SupCategrory.class)
                        .build();


        adabtor =
                new FirebaseRecyclerAdapter<Data_SupCategrory, SupCategory_ViewHolder>(options) {
                    @NonNull
                    @Override
                    public SupCategory_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sup_categroy, parent, false);
                        return new SupCategory_ViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull SupCategory_ViewHolder viewHolder, int position, Data_SupCategrory model) {
                        final String postKey = getRef(viewHolder.getAdapterPosition()).getKey();

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        mSwipeRefreshLayout.setRefreshing(false);

                        viewHolder.discription.setText("Description : "+model.getDescription().toString());
                        viewHolder.name.setText("Name : "+model.getName().toString());
                        viewHolder.price.setText("Price : "+model.getPrice().toString());
                        viewHolder.discount.setText("Discount : "+model.getDiscount().toString());
                        Picasso.get().load(model.getImage()).into(viewHolder.images);

                        viewHolder.setItemOnClickListener(new itemOnClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent=new Intent(getApplicationContext(), ProductDetails.class);
                                intent.putExtra("id",getRef(position).getKey());
                                //intent.putExtra("true",);
                                startActivity(intent);
                            }
                        });

                        viewHolder.deleat_banner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deletbanner(postKey);
                            }
                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                // calling on cancelled method when we receive
                                // any error or we are not able to get the data.
                                Toast.makeText(SupCategory.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                };
        adabtor.startListening();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adabtor);
        recyclerView.setAdapter(adabtor);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void deletbanner(final String postke) {

        final AlertDialog.Builder builder=new AlertDialog.Builder(SupCategory.this);
        builder.setTitle("Do you want delete !");
        builder.setMessage("Do you want delete this item ");

        builder.setIcon(R.drawable.ic_shopping);
        builder.setPositiveButton("Delete ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
                    databaseReference.child(postke).removeValue();

                }else{
                    Toast.makeText(SupCategory.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }


            }
//                        dialogInterface.dismiss();

        });

        builder.setNegativeButton("no ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.show();

    }


    private void show_Image() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Pick_Image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_Image && resultCode == RESULT_OK
                && data != null && data.getData() !=null){

            saveUri=data.getData();
            bu_select.setText("Image Select");

        }

    }

    private void upoladeImage(final String name, final String description,
                              final String price, final String descunte,final String quantity, final DialogInterface dialogInterface) {

        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            if (saveUri != null){

                mDialog=new ProgressDialog(SupCategory.this);
                mDialog.setMessage("Upload...");
                mDialog.show();
                mDialog.setCanceledOnTouchOutside(false);

                String imagename= UUID.randomUUID().toString();
                final StorageReference imgeFolder=storageReference.child("image/"+imagename);
                imgeFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imgeFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
                                HashMap<String, String> usermap = new HashMap<>();
                                usermap.put("name", name);
                                usermap.put("description", description);
                                usermap.put("price", price);
                                usermap.put("menuId", idCat);
                                usermap.put("discount", descunte);
                                usermap.put("quantity", quantity);
                                usermap.put("image",uri.toString());

                                databaseReference.push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), SupCategory.class);
                                            intent.putExtra("id",idCat);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            Toast.makeText(SupCategory.this, "add", Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                            dialogInterface.dismiss();
                                            saveUri=null;
                                            finish();
                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(SupCategory.this, "trey again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            }else{
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(SupCategory.this, "Check Your Network", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals("UpDate")){
            showUpladeDiloge(adabtor.getRef(item.getOrder()).getKey(),adabtor.getItem(item.getOrder()));
        }
        else  if (item.getTitle().equals("Add to Banner")){

            ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                AddToBanner(adabtor.getRef(item.getOrder()).getKey(),adabtor.getItem(item.getOrder()));
            }else{
                Toast.makeText(SupCategory.this, "Check Your Network", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onContextItemSelected(item);
    }

    private void AddToBanner(final String key, final Data_SupCategrory item) {

        String id_topish=String.valueOf(System.currentTimeMillis());
        //databaseReference.child(key).removeValue();
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("id", id_topish);
        usermap.put("image", item.getImage());
        usermap.put("name", item.getName());


        DatabaseReference databaseReference_banner;
        databaseReference_banner= FirebaseDatabase.getInstance().getReference().child("Banner");
        databaseReference_banner.child(id_topish).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Intent intent = new Intent(getApplicationContext(), SupCategory.class);
//                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("id",idCat);
//                    startActivity(intent);
                    Toast.makeText(SupCategory.this, "add", Toast.LENGTH_SHORT).show();
//                    mDialog.dismiss();
//                    finish();
                    //dialogInterface.dismiss();
                    saveUri=null;
                } else {
                    mDialog.dismiss();
//                                email.setError("Set your Data must have @ and .com");
//                                password.setError("Must password greater than 6 ");
                    Toast.makeText(SupCategory.this, "trey again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void showUpladeDiloge(final String key, final Data_SupCategrory item) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(SupCategory.this);
        builder.setTitle("One More Step!");
        builder.setMessage("Enter Your Address");

        LayoutInflater inflater=SupCategory.this.getLayoutInflater();
        View add_menu=inflater.inflate(R.layout.add_product,null);

        name=add_menu.findViewById(R.id.ed_nmaefood_home);
        name.setText(item.getName());
        quantity=add_menu.findViewById(R.id.ed_quantity_home);
        quantity.setText(item.getQuantity());
        discrption =add_menu.findViewById(R.id.ed_decriptionfood_home);
        discrption.setText(item.getDescription());
        price=add_menu.findViewById(R.id.ed_pricefood_home);
        price.setText(item.getPrice());
        descount=add_menu.findViewById(R.id.ed_nmaemenu_home);
        descount.setText(item.getDiscount());

        bu_select=add_menu.findViewById(R.id.bu_selectimage_home);
        bu_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_Image();
            }
        });



        builder.setView(add_menu);
        builder.setIcon(R.drawable.ic_shopping);
        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (name.getText().toString().isEmpty()&&price.getText().toString().isEmpty()){
                    name.setError("Enter Name Product !!");
                    name.setFocusable(true);

                }else {
                    ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
                        item.setName(name.getText().toString().trim());
                        item.setQuantity(quantity.getText().toString().trim());
                        item.setDescription(discrption.getText().toString().trim());
                        item.setDiscount(descount.getText().toString().trim());
                        item.setMenuId(key);
                        item.setPrice(price.getText().toString().trim());
                        ChangeImageCat(key,item,dialogInterface);
                    }else{
                        Toast.makeText(SupCategory.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                    }

                }
                //dialogInterface.dismiss();
            }


        });

        builder.show();

    }

    private void ChangeImageCat(final String key, final Data_SupCategrory item, final DialogInterface dialogInterface) {
        mDialog=new ProgressDialog(SupCategory.this);
        mDialog.setMessage("Upload...");
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        if (saveUri != null){
            String imagename= UUID.randomUUID().toString();
            final StorageReference imgeFolder=storageReference.child("image/"+imagename);
            imgeFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imgeFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
                            HashMap<String, String> usermap = new HashMap<>();
                            usermap.put("name", item.getName());
                            usermap.put("description", item.getDescription());
                            usermap.put("price", item.getPrice());
                            usermap.put("menuId", idCat);
                            usermap.put("discount", item.getDiscount());
                            usermap.put("quantity", item.getQuantity());
                            usermap.put("image",uri.toString());

                            databaseReference.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), SupCategory.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("id",idCat);
                                        startActivity(intent);
                                        Toast.makeText(SupCategory.this, "add", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                        finish();
                                        dialogInterface.dismiss();
                                        saveUri=null;
                                    } else {
                                        mDialog.dismiss();
//                                email.setError("Set your Data must have @ and .com");
//                                password.setError("Must password greater than 6 ");
                                        Toast.makeText(SupCategory.this, "trey again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }
            });


        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
            HashMap<String, String> usermap = new HashMap<>();
            usermap.put("name", item.getName());
            usermap.put("description", item.getDescription());
            usermap.put("price", item.getPrice());
            usermap.put("menuId", idCat);
            usermap.put("discount", item.getDiscount());
            usermap.put("quantity", item.getQuantity());
            usermap.put("image",item.getImage());

            databaseReference.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), SupCategory.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id",idCat);
                        startActivity(intent);
//                        Toast.makeText(SingelItem.this, "add", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        dialogInterface.dismiss();
                        saveUri=null;
                        finish();
                    } else {
                        mDialog.dismiss();
//                                email.setError("Set your Data must have @ and .com");
//                                password.setError("Must password greater than 6 ");
                        Toast.makeText(SupCategory.this, "trey again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }



}
