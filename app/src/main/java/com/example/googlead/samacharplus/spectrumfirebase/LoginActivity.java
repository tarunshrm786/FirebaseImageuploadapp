package com.example.googlead.samacharplus.spectrumfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText emailBox,passBox;
    public Button openSignUp,signInBtn,forgot;
    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Firease reference*/
        auth=FirebaseAuth.getInstance();

        /*Reference*/
        emailBox=findViewById(R.id.emailBox);
        passBox=findViewById(R.id.passbox);

        signInBtn=findViewById(R.id.signInBtn);
        openSignUp=findViewById(R.id.openSignup);
        forgot=findViewById(R.id.forget);

        /*BindingEvent*/
        signInBtn.setOnClickListener(this);
        openSignUp.setOnClickListener(this);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailBox.getText().toString();

                if (email.isEmpty())
                {
                    emailBox.setError("Enter Email address");
                    emailBox.requestFocus();
                }else {
                    /*Send email to reset passoword.*/
                    final ProgressDialog  progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Sending Password reset email...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Email has een send.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        int viewID=view.getId();

        if(viewID==R.id.signInBtn){

//            emailBox  = findViewById(R.id.emailBox);
  //          passBox  = findViewById(R.id.passBox);

            String email = emailBox.getText().toString();
            String pass = passBox.getText().toString();

           // final EditText classEdit = (EditText) findViewById(R.id.emailBox);
            //final EditText gradeEdit = (EditText) findViewById(R.id.passBox);

            if(email.isEmpty()){
                emailBox.setError("Enter email address.");
                emailBox.requestFocus();

            }else if(pass.isEmpty()){
                passBox.setError("Enter password");
                passBox.requestFocus();

            }else {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                /*ProgressDialog for signin progress*/
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Validating user....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if(viewID==R.id.openSignup){
            startActivity(new Intent(this,SignupActivity.class));
            finish();
        }
    }
}
