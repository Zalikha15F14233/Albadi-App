package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database_local.Database_order_local;

public class MainActivity extends AppCompatActivity {


    Button signIn,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Database_order_local database_allnotes;
//        database_allnotes=new Database_order_local(MainActivity.this);
//        database_allnotes.remove();
        signup=findViewById(R.id.bu_sighnup_main);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));

            }
        });


        signIn=findViewById(R.id.bu_login_main);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
}