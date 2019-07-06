package com.example.googlead.samacharplus.spectrumfirebase;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity implements Runnable {

    public Handler handler;
    public FirebaseAuth auth;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*FireBase Auth reference*/
        auth=FirebaseAuth.getInstance();

       // user = auth.getCurrentUser();

        /*Handler*/
        handler=new Handler();
        handler.postDelayed(this, 2000);
    }

    @Override
    public void run() {

        if(auth.getCurrentUser()!=null){

            startActivity(new Intent(this,HomeActivity.class));
            finish();

        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        handler.removeCallbacks(this);
    }
}
