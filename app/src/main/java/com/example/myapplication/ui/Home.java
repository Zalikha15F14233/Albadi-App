package com.example.myapplication.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.InterFace.itemOnClickListener;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.MenuViewHolder;
import com.example.myapplication.model.Data_Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    TextView nameuser,textViewMail;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseDatabase firebaseDatabase;


    FirebaseAuth auth;
    private FirebaseRecyclerAdapter<Data_Category, MenuViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            final AlertDialog.Builder builder=new AlertDialog.Builder(Home.this);
//                builder.setTitle("One More Step!");
            builder.setMessage("Enter Your Address");

            LayoutInflater inflater=Home.this.getLayoutInflater();
            View add_menu=inflater.inflate(R.layout.add_new_menu,null);

            final EditText edtName=add_menu.findViewById(R.id.ed_nmaemenu_home);
            bu_select=add_menu.findViewById(R.id.bu_selectimage_home);
            bu_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_Image();
                }
            });


            builder.setView(add_menu);
//                builder.setIcon(R.drawable.ic_shopping);
            builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (edtName.getText().toString().isEmpty()){
                        edtName.setError("Enter Name Food !!");
                        edtName.setFocusable(true);

                    }else {
                        upoladeImage(edtName.getText().toString().trim(),dialogInterface);
                    }
//                        dialogInterface.dismiss();
                }


            });

            builder.show();
        });



        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        View view=navigationView.getHeaderView(0);
        nameuser=view.findViewById(R.id.nav_header_title);
        textViewMail=view.findViewById(R.id.textView);
//        nameuser.setText(Comment.currentUser.getName());
        nameuser.setText(firebaseUser.getDisplayName());
        textViewMail.setText(firebaseUser.getEmail());


        recyclerView=findViewById(R.id.recycal_home);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        databaseReference= FirebaseDatabase.getInstance().getReference().child("category");
        //databaseReference.keepSynced(true);//to catch data if net offline
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("category");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(Home.this);
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
                    Toast.makeText(Home.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    public void onRefresh() {
        startUI();
    }


    private void startUI() {
        FirebaseRecyclerOptions<Data_Category> options =
                new FirebaseRecyclerOptions.Builder<Data_Category>()
                        .setQuery(databaseReference, Data_Category.class)
                        .build();


//        FirebaseRecyclerAdapter<Data_Category, MenuViewHolder>
                firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Data_Category, MenuViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, Data_Category model) {
                        final String postKey = getRef(viewHolder.getAdapterPosition()).getKey();
//                        Toast.makeText(Home.this, "???"+ postKey, Toast.LENGTH_SHORT).show();

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                Toast.makeText(Home.this, "!!!!"+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();

                                mSwipeRefreshLayout.setRefreshing(false);
                                viewHolder.posttext.setText(model.getName());
                                Picasso.get().load(model.getImage()).into(viewHolder.images);

                                viewHolder.setItemOnClickListener((view, position1, isLongClick) -> {
                                    Intent intent=new Intent(getApplicationContext(), SupCategory.class);
                                    intent.putExtra("id",""+getRef(position).getKey());

                                    startActivity(intent);
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                // calling on cancelled method when we receive
                                // any error or we are not able to get the data.
                                Toast.makeText(Home.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
                        return new MenuViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
      //  mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(getApplicationContext(), My_Orders.class));
        }else if (id == R.id.nav_Favorite) {
            startActivity(new Intent(getApplicationContext(), Favorites.class));
        }
        else if (id == R.id.nav_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }
        else if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(), Profile.class));
        }
        else if (id == R.id.nav_logout) {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser!=null){
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                finish();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    StorageReference storageReference;
    FirebaseStorage storage;

    private void upoladeImage(final String name, final DialogInterface dialogInterface) {

        if (saveUri != null){

            final ProgressDialog mDialog=new ProgressDialog(Home.this);
            mDialog.setMessage("Uploade...");
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
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("category");
                            HashMap<String, String> usermap = new HashMap<>();
                            usermap.put("name", name);
                            usermap.put("image",uri.toString());

                            databaseReference.push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        Intent intent = new Intent(getApplicationContext(), Home.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
                                        Toast.makeText(Home.this, "add", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                        dialogInterface.dismiss();
                                        saveUri=null;
//                                        finish();
                                    } else {
                                        mDialog.dismiss();
//                                email.setError("Set your Data must have @ and .com");
//                                password.setError("Must password greater than 6 ");
                                        Toast.makeText(Home.this, "trey agin", Toast.LENGTH_SHORT).show();
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
    }

    private void show_Image() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Slect Picticer"),Pick_Image);
    }
    Uri saveUri;
    Button bu_select;
    final int Pick_Image=71;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_Image && resultCode == RESULT_OK
                && data != null && data.getData() !=null){

            saveUri=data.getData();
            bu_select.setText("Image Select");

        }

    }
}
