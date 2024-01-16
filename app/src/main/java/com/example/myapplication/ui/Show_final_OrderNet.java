package com.example.myapplication.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.text.font.Font;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.geom.PageSize;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import android.os.Environment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;




import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;

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
    TextView idorder,location,totalText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_final_order_net);

        Permeation permeation=new Permeation();
        permeation.checkAndRequestPermissions(Show_final_OrderNet.this);


        String invoiceContent = "Invoice\n\nItem 1: $10\nItem 2: $20\nTotal: $30";

        // Generate PDF with the given content
        PdfGenerator.generateInvoice("invoice.pdf", invoiceContent);


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


        idorder=findViewById(R.id.text_idorder_finalorder);
        idorder.setText("id order : "+idorder_string);
        location=findViewById(R.id.text_location_finalorder);
        totalText=findViewById(R.id.text_total_finalorder);
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
                        .setQuery(databaseReference.child(idorder_string).child("products"), DataOrderLocal.class)
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
                        giftitemPOJO.setImageurl(model.getImageurl());
                        giftitemPOJO.setCount(model.getCount());
                        giftitemPOJO.setId(model.getId());
                                giftitemPOJO.setProdectamount(model.getProdectamount());
                        giftitemPOJO.setProdectDiscount(model.getProdectDiscount());
                        giftitemPOJO.setProdectid(model.getProdectid());
                                giftitemPOJO.setProdectname(model.getProdectname());
                                giftitemPOJO.setProdectPrice(model.getProdectPrice());
//                        giftitemPOJO.setTotalprice(model.getTotalprice());

                                MyList1.add(giftitemPOJO);
                                double x=0.0;
                                for(int i=0;i<MyList1.size();i++){
                                    x=x+(
                                            (Double.parseDouble(MyList1.get(i).getProdectPrice().toString())
                                                    -
                                                    Double.parseDouble(MyList1.get(i).getProdectDiscount().toString()))
                                                    *
                                            Double.parseDouble(MyList1.get(i).getProdectamount().toString())
                                    );
                                }
                                totalText.setText("Total : "+x);
//                                Toast.makeText(Show_final_OrderNet.this, ""+x, Toast.LENGTH_SHORT).show();
                                String postkesaa=dataSnapshot.getRef().getKey();
                                Log.e("json","json: _________>" + postkesaa);

                                mSwipeRefreshLayout.setRefreshing(false);

                                viewHolder.price.setText("price : "+model.getProdectPrice());
                                viewHolder.numfood.setText("amount Product : "+model.getProdectamount());
                                viewHolder.namefood.setText("name Product : "+model.getProdectname());
                                viewHolder.text_des.setText("Discount : "+model.getProdectDiscount());
                                Picasso.get().load(model.getImageurl()).into(viewHolder.images);

                                viewHolder.imageprint.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createPdfWrapper();
                                    }
                                });
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







    /// to print fatora in pdf


//    private void createPdfWrapper() throws FileNotFoundException {
//
//        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
//                    showMessageOKCancel("You need to allow access to Storage",
//                            (dialog, which) -> {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                            REQUEST_CODE_ASK_PERMISSIONS);
//                                }
//                            });
//                    return;
//                }
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        REQUEST_CODE_ASK_PERMISSIONS);
//            }
//            return;
//        } else {
//            createPdf();
//        }
//    }



//private void createPdfWrapper() {
//    int hasWriteStoragePermission =
//            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//    if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
//        // Permission already granted, proceed to create PDF
//        createPdf();
//    } else {
//        // Permission not granted, request it
//        requestWriteStoragePermission();
//    }
//}
//
//    private void requestWriteStoragePermission() {
////        ActivityCompat.requestPermissions(this,
////                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                REQUEST_CODE_ASK_PERMISSIONS);
//
//        ActivityCompat.requestPermissions(this,
//                new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                },
//                REQUEST_CODE_ASK_PERMISSIONS);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed to create PDF
//                createPdf();
//            } else {
//                // Permission denied, show a message or handle accordingly
//                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(context)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }


    private void createPdfWrapper() {
        createPdf();
//        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
//            // Permission already granted, proceed to create PDF
//            createPdf();
//        } else {
////            requestWriteStoragePermission();
//            // Permission not granted, check if user has selected "Don't ask again"
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // User has selected "Don't ask again"
//                // Guide the user to app settings
//                showPermissionSettingsDialog();
//            } else {
//                // Request permission
//                requestWriteStoragePermission();
//            }
//        }
    }

    private void requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(Show_final_OrderNet.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to create PDF
                createPdf();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPermissionSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("To use this feature, you need to grant storage permission. Please go to app settings and enable the permission.")
                .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void createPdf()  {
//        File docsFolder = new File(Environment.getExternalStorageDirectory() + "diaa");
        File docsFolder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/adiaa");

        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Toast.makeText(context, "Created a new directory for PDF", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Created a new directory for PDF");
        }

        Toast.makeText(context, "This Folder Saved in file diaa on device ", Toast.LENGTH_SHORT).show();
//        String pdfname = "FoodDiaa_" + idorder_string + ".pdf";
        String pdfname = "MyOrder_" + idorder_string + ".pdf";
        File pdfFile = new File(docsFolder, pdfname);

        try (OutputStream output = new FileOutputStream(pdfFile);
             PdfWriter writer = new PdfWriter(output);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument, PageSize.A4)) {

            Table table = new Table(new float[]{3, 3, 3,3, 3});
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(createCell("Name", ColorConstants.GRAY));
            table.addHeaderCell(createCell("Price", ColorConstants.GRAY));
            table.addHeaderCell(createCell("Amount", ColorConstants.GRAY));
            table.addHeaderCell(createCell("Discount", ColorConstants.GRAY));
            table.addHeaderCell(createCell("Total", ColorConstants.GRAY));

            for (int i = 0; i < MyList1.size(); i++) {
                DataOrderLocal product = MyList1.get(i);

                String productName = product.getProdectname();
                String productPrice = product.getProdectPrice();
                String productAmount = product.getProdectamount();

                double productDiscount = Double.parseDouble(product.getProdectDiscount()) * Double.parseDouble(productAmount);

                double total = (Double.parseDouble(productPrice) * Double.parseDouble(productAmount))-productDiscount;

                table.addCell(createCell(productName));
                table.addCell(createCell(productPrice));
                table.addCell(createCell(productAmount));
                table.addCell(createCell(String.valueOf(productDiscount)));
                table.addCell(createCell(String.valueOf(total)));
            }

            PdfFont titleFont = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            PdfFont normalFont = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Paragraph title = new Paragraph("MyOrder").setFont(titleFont).setFontSize(30).setUnderline().setFontColor(ColorConstants.BLUE);
//            Paragraph title = new Paragraph("food diaa").setFont(titleFont).setFontSize(30).setUnderline().setFontColor(Color.BLUE);
            Paragraph orderId = new Paragraph("id order : " + idorder_string).setFont(normalFont).setFontSize(20).setFontColor(ColorConstants.BLUE);
            Paragraph location = new Paragraph("Location user : " + location_string).setFont(normalFont).setFontSize(20).setFontColor(ColorConstants.BLUE);
            Paragraph totalOrder = new Paragraph("Total Order : " + total_string).setFont(normalFont).setFontSize(20).setFontColor(ColorConstants.BLUE);

            document.add(title);
            document.add(orderId);
            document.add(location);
            document.add(totalOrder);
            document.add(table);

        } catch (IOException e) {
            Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("ss",e.toString());
            Log.d("ss",e.toString());
            Log.d("ss","-----------------------------------------------");
            e.printStackTrace();
        }

        previewPdf(pdfFile);
    }

    private Cell createCell(String text) {
        return new Cell().add(new Paragraph(text));
    }

    private Cell createCell(String text, com.itextpdf.kernel.colors.Color backgroundColor) {
        return new Cell().add(new Paragraph(text)).setBackgroundColor(backgroundColor);
    }

//    private void previewPdf() {
//        PackageManager packageManager = context.getPackageManager();
//        Intent testIntent = new Intent(Intent.ACTION_VIEW);
//        testIntent.setType("application/pdf");
//        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        if (list.size() > 0) {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(pdfFile);
//            intent.setDataAndType(uri, "application/pdf");
//            startActivity(intent);
//        } else {
//            Toast.makeText(context, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
//        }
//    }
//private void previewPdf() {
//    PackageManager packageManager = context.getPackageManager();
//    Intent testIntent = new Intent(Intent.ACTION_VIEW);
//    testIntent.setType("application/pdf");
//    List<ResolveInfo> list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
//
//    if (list.size() > 0) {
//        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);
//
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/pdf");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Ensure permission is granted
//
//        startActivity(intent);
//    } else {
//        Toast.makeText(context, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
//    }
//}

    private void previewPdf(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }
}





