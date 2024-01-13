package com.example.myapplication.database_local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.DataOrderLocal;

import java.util.ArrayList;

public class Database_order_local {

        private final static String DATABASE_NAME = "ORDERPRODUCT";
        private final static String DATABASE_Table = "orderProduct";
        private final int DATABASE_VERSION = 2;
        private final String KEY_ID = "_id";

        private final String KEY_prodectID = "_prodectID";
        private final String KEY_imageurl = "_imageurl";
        private final String KEY_prodectname = "_prodectname";
        private final String KEY_prodectamunt = "_prodectamunt";
        private final String KEY_prodectprice = "_prodectprice";
        private final String KEY_prodectdiscription = "_prodectdiscription";

        private final Context context;

        SQLiteDatabase sqLiteDatabase;
        DBhelber dBhelber;

        public Database_order_local(Context context) {
            this.context = context;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        private class DBhelber extends SQLiteOpenHelper {
            public DBhelber(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + DATABASE_Table + "(" + KEY_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_prodectID
                        + " TEXT NOT NULL,"+ KEY_prodectamunt
                        + " TEXT NOT NULL,"+ KEY_prodectdiscription
                        + " TEXT NOT NULL,"+ KEY_prodectprice
                        + " TEXT NOT NULL," + KEY_prodectname
                        + " TEXT NOT NULL," + KEY_imageurl
                        + " TEXT NOT NULL);"
                );

            }


            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("Drop Table iF Exists " + DATABASE_Table);
                onCreate(db);

            }
        }
        public void open() {
            //use to open class Dbhebel and cinect with databasehelber
            dBhelber = new DBhelber(context);
            sqLiteDatabase = dBhelber.getWritableDatabase();
            sqLiteDatabase=dBhelber.getReadableDatabase();
        }

        public void insert(String name,String id,String amunt,String price,String discription,String imageurl) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_prodectname,name);
            contentValues.put(KEY_prodectID,id);
            contentValues.put(KEY_imageurl,imageurl);
            contentValues.put(KEY_prodectamunt, amunt);
            contentValues.put(KEY_prodectprice, price);
            contentValues.put(KEY_prodectdiscription, discription);

            sqLiteDatabase.insert(DATABASE_Table, null, contentValues);
             //Log.e("zdiaax>>>>>>>>>>>>",discription);
        }


        public boolean chkDB(){
            boolean chk = false;
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_Table, null);

            if (cursor!=null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    chk = true;
                }
            }
            else{
                chk = false;
            }
            return chk;
        }

        @SuppressLint("Range")
        public ArrayList getallarrylit_check() {//to get last row
            ArrayList<DataOrderLocal> products = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderProduct", null);
            if (cursor!=null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    DataOrderLocal product = new DataOrderLocal();
                    product.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));;
                    //product.setProdectname(""+cursor.getCount());
                    product.setProdectname(cursor.getString(cursor.getColumnIndex(KEY_prodectname)));
                    product.setProdectid(cursor.getString(cursor.getColumnIndex(KEY_prodectID)));
                    product.setProdectamount(cursor.getString(cursor.getColumnIndex(KEY_prodectamunt)));
                    product.setProdectPrice(cursor.getString(cursor.getColumnIndex(KEY_prodectprice)));
                    product.setProdectDiscount(cursor.getString(cursor.getColumnIndex(KEY_prodectdiscription)));
                    product.setImageurl(cursor.getString(cursor.getColumnIndex(KEY_imageurl)));
                    products.add(product);
                }
            }else {
                DataOrderLocal product = new DataOrderLocal();
                product.setId("");
                products.add(product);
            }
            return products;

        }

//    DELETE FROM customers

        public void remove() {
        // String deleat=String.format("DELETE FROM Data_orderFood");
        //sqLiteDatabase.execSQL(deleat);
        context.deleteDatabase(DATABASE_NAME);
    }


    public boolean remove_item(String id_item) {
        boolean chk = false;
        Cursor cursor = sqLiteDatabase.rawQuery(" DELETE  FROM orderProduct"+ " WHERE "
                + KEY_ID + " = '"+ id_item + "'" , null);

        if (cursor!=null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                chk = true;
            }
        }
        else{
            chk = false;
        }
        return chk;

    }



        public boolean cek_isExist_orNot(String id) {//to get last row
            ArrayList<DataOrderLocal> products = new ArrayList<>();
            //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM importnote"+ " WHERE " + KEY_phone + " = '"+ phone + "'" + "  ORDER BY _id DESC LIMIT 1", null);
            //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderFood", null);

            boolean chk = false;
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderProduct"+ " WHERE " + KEY_prodectID + " = '"+ id + "'" + "  ORDER BY _id DESC LIMIT 1", null);

            if (cursor!=null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    chk = true;
                }
            }
            else{
                chk = false;
            }
            return chk;

        }
//
//        public ArrayList getallarrylit_singelrow(String phone) {
//            phone="uploded";
//            ArrayList<Data_notes> products = new ArrayList<>();
//            // Cursor cursornum = sqLiteDatabase.rawQuery("SELECT * FROM importnote"+ " WHERE " + KEY_phone + " = '"+ phone + "'" + "", null);
//            Cursor cursor = sqLiteDatabase.rawQuery( "SELECT * FROM " + DATABASE_Table + " WHERE " + KEY_satesnote + " = '"+ "uploded" + "'" + " ORDER BY _id  ", null);
//
//            //Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM azllemails where" + KEY_phone + " = '"+ phone + "'" + " ORDER BY _id DESC LIMIT 1", null);
//            if (cursor!=null) {
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                    Data_notes product = new Data_notes();
//                    product.setLastrow(cursor.getString(cursor.getColumnIndex(KEY_IDnet)));
//                    product.setIdnetsingel(cursor.getString(cursor.getColumnIndex(KEY_IDnet)));
//                    product.setNotesingel(cursor.getString(cursor.getColumnIndex(KEY_note)));
//                    product.setNamecustmersingel(cursor.getString(cursor.getColumnIndex(KEY_namecustmer)));
//                    product.setSatesnotesingel(cursor.getString(cursor.getColumnIndex(KEY_satesnote)));
//                    product.setPhonesingel(cursor.getString(cursor.getColumnIndex(KEY_phone)));
//                    product.setCountdatabase_allnotes(""+cursor.getCount());
////                Log.v("zdiaacu",""+cursornum.getCount());
//                    products.add(product);
//                }
//            }else {
//                Data_notes product = new Data_notes();
//                product.setLastrow("");
//                products.add(product);
//            }
//            return products;
//
//        }
//
//
//
//
        public boolean up_data(String amount ,String id ){
            //SQLiteDatabase db=this.getWritableDatabase();
            ContentValues contentValues=new ContentValues();
            contentValues.put(KEY_prodectamunt,amount);

            // db.update("mytable",contentValues,"id=?",new String[]{id});
            sqLiteDatabase.update(DATABASE_Table, contentValues, KEY_ID + "=" + id, null);
            // Cursor cursor = sqLiteDatabase.rawQuery("UPDATE importnote "+ " set  " + KEY_satesnote + " = '"+ sates + "'" + " WHERE  " + KEY_ID + " = '"+ id + "'" + "", null);
//        Toast.makeText(context, ""+id, Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, ""+sates, Toast.LENGTH_SHORT).show();
            return  true;
        }
//
//
}
