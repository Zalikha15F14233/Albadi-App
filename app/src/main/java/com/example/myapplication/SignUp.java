package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {


    EditText email,password,name,phone;
    Button login;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawableResource(R.drawable.bacalb);
        setContentView(R.layout.activity_sign_up);


        auth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("User");

        email=findViewById(R.id.ed_email_logup);
        phone=findViewById(R.id.ed_phone_logup);
        name=findViewById(R.id.ed_name_logup);
        password=findViewById(R.id.ed_password_logup);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        login=findViewById(R.id.ed_login_logup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    loginwithFirebase();
                }else{
                    Toast.makeText(SignUp.this, "Check Your Network", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }




    private void loginwithFirebase() {

        if (!validateEmail() |  !validatePassword()) {
            //  String input = "Email: " + textInputEmail.getEditText().getText().toString();
            return;
        }else {

            String emails=email.getText().toString().trim();
            String passwords=password.getText().toString().trim();
            final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setTitle("Register User");
            progressDialog.setMessage("Please Waite ........");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            auth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Toast.makeText(Regester.this, "to caret", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = firebaseUser.getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                        HashMap<String, String> usermap = new HashMap<>();
                        usermap.put("email", email.getText().toString().trim());
                        usermap.put("phone",phone.getText().toString().trim());
                        usermap.put("id", uid);
                        usermap.put("is_stuff", "false");
                        usermap.put("states", "Hi there, Iam use Chat App");
                        databaseReference.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    Toast.makeText(SignUp.this, "add database", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    email.setError("Set your Data must have @ and .com");
                                    password.setError("Must password greater than 6 ");
                                    Toast.makeText(SignUp.this, "trey again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        email.setError("Set your Data must have @ and .com");
                        password.setError("Must password greater than 6 ");
                        Toast.makeText(getApplicationContext(), "You get some error ", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
            });

        }

    }


    private boolean validatePassword() {
        String passwordInput = password.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            password.setError("Field can't be empty");
            password.setFocusable(true);
            return false;
        }
        else if (passwordInput.length()<6) {
            password.setError("Password length must be more 6 string");
            return false;
        }
        else {
            password.setError(null);
            return true;
        }
    }
    private boolean validateEmail() {
        String emailInput = email.getText().toString().trim();

        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid email address Like ex@gmail.com");
            return false;
        } else {
            email.setError(null);
            checkEmail_Is_Esxist(email.getText().toString().trim());
            return true;
        }
    }

    private void checkEmail_Is_Esxist(String emails) {
        auth.fetchSignInMethodsForEmail(emails)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            Log.e("TAG", "Is New User!");
                        } else {
                            Log.e("TAG", "Is Old User!");
                        }

                    }
                });
    }

}