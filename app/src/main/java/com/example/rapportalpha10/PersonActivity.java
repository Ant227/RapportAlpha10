package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PersonActivity extends AppCompatActivity {

    private ImageView PersonUserImage;
    private TextView PersonUsername, PersonUserJob, PersonUserCity, PersonSama, PersonUniversity, PersonDegree;
    private Button PersonPosts;

    private Toolbar mToolbar;

    private String PersonUserID;
    private DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

       SettingUpToolbar();

        PersonUserID = getIntent().getExtras().get("person_user_id").toString();

        Initialization();

        RetrievePersonInformation();


        PersonPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToMyPostsActivity();
            }
        });



    }

    private void SendUserToMyPostsActivity()
    {
        Intent MyPostIntent = new Intent(PersonActivity.this, MyPostsActivity.class);
        MyPostIntent.putExtra("person_user_id", PersonUserID);
        startActivity(MyPostIntent);
    }

    private void RetrievePersonInformation()
    {
        UsersRef.child(PersonUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(PersonActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(PersonUserImage);
                    }

                    if (dataSnapshot.hasChild("A_name")) {
                        String Uname = dataSnapshot.child("A_name").getValue().toString();
                        PersonUsername.setText("Dr." + Uname);
                    }
                    if (dataSnapshot.hasChild("B_job")) {
                        String Ujob = dataSnapshot.child("B_job").getValue().toString();
                        PersonUserJob.setText("Job: "+Ujob);
                    }
                    if (dataSnapshot.hasChild("C_city")) {
                        String Ucity = dataSnapshot.child("C_city").getValue().toString();
                        PersonUserCity.setText("City: "+Ucity);
                    }

                    if (dataSnapshot.hasChild("D_samaNumber")) {
                        String Usama = dataSnapshot.child("D_samaNumber").getValue().toString();
                        PersonSama.setText("Sama: "+Usama);
                    }

                    if (dataSnapshot.hasChild("G_degree")) {
                        String Udegree = dataSnapshot.child("G_degree").getValue().toString();
                        PersonDegree.setText("Degree: "+Udegree);
                    }
                    if (dataSnapshot.hasChild("E_university")) {
                        String Uuniversity = dataSnapshot.child("E_university").getValue().toString();
                        PersonUniversity.setText(Uuniversity);
                    }
                    else {
                        Toast.makeText(PersonActivity.this, "Values don't exit", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initialization()
    {
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        PersonUserImage          = (ImageView)      findViewById(R.id.person_user_image);
        PersonUsername           = (TextView)       findViewById(R.id.person_username);
        PersonUserJob            = (TextView)       findViewById(R.id.person_user_job);
        PersonUserCity           = (TextView)       findViewById(R.id.person_user_city);

        PersonSama               = (TextView)       findViewById(R.id.person_sama);
        PersonUniversity         = (TextView)       findViewById(R.id.person_university);
        PersonDegree             = (TextView)       findViewById(R.id.person_degree);

        PersonPosts              = (Button)         findViewById(R.id.person_posts_button);
    }

    private void SettingUpToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.person_app_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }
}
