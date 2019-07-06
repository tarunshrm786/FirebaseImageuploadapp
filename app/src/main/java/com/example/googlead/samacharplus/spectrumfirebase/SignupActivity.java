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

public class SignupActivity<viewID> extends AppCompatActivity implements View.OnClickListener {

    public EditText emailBox,passBox;
    public Button openSignIn,signupBtn;
//    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /*Reference*/
       // name=findViewById(R.id.nameBox);
        emailBox=findViewById(R.id.emailBox);
        passBox=findViewById(R.id.passBox);
        signupBtn=findViewById(R.id.signUpBtn);
        openSignIn=findViewById(R.id.openSignin);


        /*BindingEvent*/
        signupBtn.setOnClickListener(this);
        openSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int viewID=view.getId();

        if(viewID==R.id.signUpBtn){

           // String namebox=name.getText().toString();
            String email=emailBox.getText().toString();
            String pass=passBox.getText().toString();

            if(email.isEmpty()){
                emailBox.setError("Enter email address.");
                emailBox.requestFocus();

            }else if(pass.isEmpty()) {
                passBox.setError("Enter password");
                passBox.requestFocus();
            }else {
                FirebaseAuth auth=FirebaseAuth.getInstance();

                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Registration in progress....");
                progressDialog.setCancelable(false);
                progressDialog.show();/*ProgressDialog for signup progress*/


                /*Creating User*/
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if(task.isSuccessful()){


                            startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                            finish();

                        }else{
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if(viewID==R.id.openSignin){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }
}
