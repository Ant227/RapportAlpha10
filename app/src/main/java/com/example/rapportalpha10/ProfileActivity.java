package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity
{
    private ImageView userProfileImage , profileEditing;

    private TextView username;
    private TextView job;
    private TextView city;

    private TextView sama;
    private TextView univsersity;
    private TextView degree;

    private Button logoutBtn , MyPostButton;

    private DatabaseReference UsersRef ;

    String current_user_id;

    private TextView VersionDetailsText;

   private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        MyPostButton = findViewById(R.id.profile_my_posts_button);

        userProfileImage = findViewById(R.id.profile_user_image);
        profileEditing = (ImageView) findViewById(R.id.profile_editing);
        username = findViewById(R.id.profile_UserName);
        job = findViewById(R.id.profile_user_Job);
        city = findViewById(R.id.profile_user_City);
        sama = findViewById(R.id.profile_sama);
        univsersity = findViewById(R.id.profile_university);
        degree = findViewById(R.id.profile_degree);
        logoutBtn = findViewById(R.id.logout_btn);
        VersionDetailsText = (TextView) findViewById(R.id.version_update_details_text);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        versionDetailsShow();


        profileEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToProfileEditActivity();
            }
        });

        MyPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToMyPostsActivity();
            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    }

                    if (dataSnapshot.hasChild("A_name")) {
                        String Uname = dataSnapshot.child("A_name").getValue().toString();
                        username.setText("Dr." + Uname);
                    }
                    if (dataSnapshot.hasChild("B_job")) {
                        String Ujob = dataSnapshot.child("B_job").getValue().toString();
                        job.setText(Ujob);
                    }
                    if (dataSnapshot.hasChild("C_city")) {
                        String Ucity = dataSnapshot.child("C_city").getValue().toString();
                        city.setText(Ucity);
                    }

                    if (dataSnapshot.hasChild("D_samaNumber")) {
                        String Usama = dataSnapshot.child("D_samaNumber").getValue().toString();
                        sama.setText(Usama);
                    }

                    if (dataSnapshot.hasChild("G_degree")) {
                        String Udegree = dataSnapshot.child("G_degree").getValue().toString();
                        degree.setText(Udegree);
                    }
                    if (dataSnapshot.hasChild("E_university")) {
                        String Uuniversity = dataSnapshot.child("E_university").getValue().toString();
                        univsersity.setText(Uuniversity);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Values don't exit", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Log Out");
                final TextView textField = new TextView((ProfileActivity.this));
                textField.setText("Are you Sure?");
                textField.setTextSize(16);
                textField.setPadding(30,30,10,10);
                textField.setTextColor(Color.parseColor("#000000"));
                builder.setView(textField);



                builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mAuth.signOut();
                        SendUserToLoginActivity();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);


            }
        });


    }

    private void SendUserToMyPostsActivity()
    {
        Intent MyPostIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        MyPostIntent.putExtra("person_user_id", current_user_id);
        startActivity(MyPostIntent);
    }


    private void sendUserToProfileEditActivity()
    {
        Intent intentProfileEdit = new Intent(ProfileActivity.this, ProfileEditActivity.class);
        startActivity(intentProfileEdit);
    }

    private void versionDetailsShow()
    {
        VersionDetailsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               SendUserToVersionActivity();

            }
        });
    }

    private void SendUserToVersionActivity()
    {
        Intent intentVersion = new Intent(ProfileActivity.this, VersionActivity.class);
        startActivity(intentVersion);
    }

    private void SendUserToLoginActivity()
    {
        Intent intentLogin = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intentLogin);
    }
}
