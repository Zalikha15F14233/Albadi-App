package com.example;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permeation {

    Context context;
    int REQUEST_ID_MULTIPLE_PERMISSIONS=11;
//READ_EXTERNAL_STORAGE
    public boolean checkAndRequestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionSTORAGE  = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            int permissionCall  = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
     int location= ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//            int camera= ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissionSTORAGE != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
//            if (permissionRECORDAUDIO != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
//            }
//            if (location != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
//                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//            }
//            if (camera != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.CAMERA);
//                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//            }
//            if (permissionsms != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
//                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//            }
//            if (permissionstorg != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//            }
            if (permissionCall != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) activity , listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }

            return true;
        }
        else
            return true;
    }


}
