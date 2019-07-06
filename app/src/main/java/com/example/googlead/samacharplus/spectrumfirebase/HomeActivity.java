package com.example.googlead.samacharplus.spectrumfirebase;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.net.URI;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* Binding Data */

    // String name=user.getDisplayName().isEmpty() ? "User Name" : user.getDisplayName();
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseUser user;
    public ImageView imageView;
    public TextView username,useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user=auth.getCurrentUser();

        if (!user.isEmailVerified())
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Please verify your email.");
            dialog.setPositiveButton("Verify email", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.sendEmailVerification();
                }
            });
            dialog.setNegativeButton("Cancel",null);
            dialog.show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Write by us*/
        String names=user.getDisplayName().isEmpty() ? "User Name" : user.getDisplayName();
        getSupportActionBar().setTitle(names);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*Refereces*/
        imageView= navigationView.getHeaderView(0).findViewById(R.id.userProfile);
        username= navigationView.getHeaderView(0).findViewById(R.id.userName);
        useremail= navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        username.setText(names);
        useremail.setText(user.getEmail());

        if (user.getPhotoUrl()==null)
        {
            Picasso.get().load(R.mipmap.ic_launcher_round);
        }
        else
        {
            Picasso.get().load(user.getPhotoUrl()).into((Target) user);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(HomeActivity.this, "photo", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1001);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1001 && data!=null)
        {
          //  data.getExtras().get("data");

            Bitmap imgsrc = (Bitmap)data.getExtras().get("data");

            /* Store on firebase */
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            imgsrc.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte image[]=baos.toByteArray();

            /*ProgressDialog for image upload progress*/
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Upload profile....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            /* Image Upload */
            final StorageReference storage = FirebaseStorage.getInstance().getReference().child("userProfiles/"+user.getUid());  /*Get object or say reference.*/

            storage.putBytes(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    progressDialog.dismiss();
                    if (task.isSuccessful())
                    {
                        /* Update Profile */
                        storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                final Uri imageUri = task.getResult();
                                UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                                user.updateProfile(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();

                                        if (task.isSuccessful())
                                        {
                                            Picasso.get().load(imageUri).into(imageView);
                                            Toast.makeText(HomeActivity.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        //Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imageView.setImageBitmap(imgsrc);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }
        else if (id == R.id.log) {

            auth.signOut();
            startActivity(new Intent(this,LoginActivity.class));
            finish();

        } else if (id == R.id.nav_slideshow) {

        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
