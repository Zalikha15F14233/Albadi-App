package com.example.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.braintreepayments.cardform.view.CardForm;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.example.myapplication.R;
import com.example.myapplication.database_local.Database_order_local;
import com.example.myapplication.model.DataOrderLocal;
import com.example.myapplication.model.DataReqeste;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class CartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {

    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<DataOrderLocal> mDataSet;

    TextView totalprice , total_discount,total_due;

    Button addtofirebase;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Database_order_local database_allnotes;

    String location_name;
    private final static int PLACE_PIKER_RESSULT=100;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    private boolean mAlreadyStartedService = false;


    // dilog
    EditText my_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setLocale("en");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");

        totalprice = findViewById(R.id.text_totalprice_cartitem);
        total_discount = findViewById(R.id.text_total_dicount_cartitem);
        total_due = findViewById(R.id.text_total_due_cartitem);


        recyclerView = findViewById(R.id.recycal_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(CartActivity.this);
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
                    mSwipeRefreshLayout.setRefreshing(false);
                    startUI();
                } else {
                    Toast.makeText(CartActivity.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });




        addtofirebase = findViewById(R.id.bu_addtofirebase_cartitem);
        addtofirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deiloglocation();

            }
        });
    }

    private void deiloglocation() {

        final AlertDialog.Builder builder1=new AlertDialog.Builder(CartActivity.this);
        builder1.setTitle("One More Step!");
//        builder1.setMessage("Enter Your location");
        builder1.setCancelable(false);

        LayoutInflater inflater=CartActivity.this.getLayoutInflater();
        View add_menu=inflater.inflate(R.layout.add_order,null);

        my_location=add_menu.findViewById(R.id.ed_me_location_addorder);
        CardForm cardForm = (CardForm) add_menu.findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")

                .setup(CartActivity.this);


        builder1.setView(add_menu);
//        builder1.setIcon(R.drawable.ic_shopping);
        builder1.setPositiveButton("Uplode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (my_location.getText().toString().isEmpty()&&my_location.getText().toString().isEmpty()){
                    my_location.setError("Enter Your location !!");
                    my_location.setFocusable(true);
                }if (!cardForm.isValid()) {
                    Toast.makeText(CartActivity.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }else {
                    database_allnotes = new Database_order_local(CartActivity.this);
                    database_allnotes.open();
                    mDataSet = database_allnotes.getallarrylit_check();
                    //String iduser, String address, String total, List<DataOrderLocal> food
                    DataReqeste dataReqeste = new DataReqeste(
                            total_due.getText().toString().trim()
                            ,total_discount.getText().toString().trim()
                            ,firebaseUser.getUid()
                            , my_location.getText().toString().trim()
                            , totalprice.getText().toString().trim()
                            , mDataSet ,
                            cardForm.getCardNumber(),
                            cardForm.getExpirationMonth(),
                            cardForm.getExpirationYear(),
                            cardForm.getCvv(),
                            cardForm.getCardholderName(),
                            cardForm.getPostalCode(),
                            cardForm.getCountryCode(),
                            cardForm.getMobileNumber()
                    );

                    databaseReference.child(String.valueOf(System.currentTimeMillis())).setValue(dataReqeste);
                    database_allnotes.remove();
                    dialogInterface.dismiss();
                    finish();
                }
            }


        });
        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }


        });

        builder1.show();

    }



    private void startUI() {
        database_allnotes = new Database_order_local(CartActivity.this);
        database_allnotes.open();
        mDataSet = database_allnotes.getallarrylit_check();

        totalprice.setText("");
        if (mDataSet.size() < 0) {
            totalprice.setText("");
            //Toast.makeText(this, "ddd"+mDataSet.size(), Toast.LENGTH_SHORT).show();
        } else {
            double x = 0.0;
            double dis = 0.0;
            for (int i = 0; i < mDataSet.size(); i++) {
                DataOrderLocal dataOrderLocal = new DataOrderLocal();
                double price = Double.parseDouble(mDataSet.get(i).getProdectPrice()) * Double.parseDouble(mDataSet.get(i).getProdectamount());
                x += price;
                totalprice.setText("" + x);

                double discount = Double.parseDouble(mDataSet.get(i).getProdectDiscount()) * Double.parseDouble(mDataSet.get(i).getProdectamount());
                dis += discount;
                total_discount.setText("" + dis);

                total_due.setText(""+(x-dis));
            }
        }

        Adabtor_databaseLocal mAdapter = new Adabtor_databaseLocal(this, mDataSet);

         mAdapter.setMode(Attributes.Mode.Single);
//        ((Adabtor_databaseLocal) mAdapter).setMode(Attributes.Mode.Single);

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mDataSet = new ArrayList<>();

    }


    @Override
    public void onRefresh() {
        startUI();
    }
    public class Adabtor_databaseLocal extends RecyclerSwipeAdapter<Adabtor_databaseLocal.SimpleViewHolder> {

        private Context mContext;
        private ArrayList<DataOrderLocal> studentList;

        public Adabtor_databaseLocal(Context context, ArrayList<DataOrderLocal> objects) {
            this.mContext = context;
            this.studentList = objects;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
            final DataOrderLocal item = studentList.get(position);

            viewHolder.name.setText(item.getProdectname());
            Picasso.get().load(item.getImageurl()).into(viewHolder.image);

            double x = Double.parseDouble(item.getProdectamount()) * Double.parseDouble(item.getProdectPrice());
            viewHolder.price.setText(item.getProdectPrice());
            viewHolder.amount.setText("Total : " + x);
            viewHolder.num.setText(item.getProdectamount());

            viewHolder.deleate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle delete button click
                    database_allnotes = new Database_order_local(mContext);
                    database_allnotes.open();
                    database_allnotes.remove_item(item.getId());
                    startUI();
                }
            });
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int x= Integer.parseInt(item.getProdectamount())+1;
                    viewHolder.num.setText(String.valueOf(x));
                    database_allnotes = new Database_order_local(mContext);
                    database_allnotes.open();

                    database_allnotes.up_data(""+viewHolder.num.getText(),item.getId());
                    startUI();
                }
            });
            viewHolder.minimize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int xx= Integer.parseInt(item.getProdectamount());
                    if(xx > 1){
                        int x= Integer.parseInt(item.getProdectamount())-1;
                        viewHolder.num.setText(String.valueOf(x));
                        database_allnotes = new Database_order_local(mContext);
                        database_allnotes.open();

                        database_allnotes.up_data(""+viewHolder.num.getText(),item.getId());
                        startUI();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return 0;
        }

        public class SimpleViewHolder extends RecyclerView.ViewHolder {

            public TextView name,num;
            public TextView price;
            public TextView amount;
            public ImageView image, deleate;
            public ImageButton add,minimize;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                add = itemView.findViewById(R.id.add);
                minimize = itemView.findViewById(R.id.minimize);
                name = itemView.findViewById(R.id.text_namefood_itemcart);
                num = itemView.findViewById(R.id.num);
                price = itemView.findViewById(R.id.text_price_itemcart);
                amount = itemView.findViewById(R.id.text_amunt_itemcart);
                image = itemView.findViewById(R.id.image_food_cart);
                deleate = itemView.findViewById(R.id.image_deleatfrom_cart);
            }
        }
    }





    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }
}

