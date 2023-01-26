package com.example.rapportalpha10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ImageView ProfileEditImageView;
    private EditText ProfileEditUserName, ProfileEditJob, ProfileEditCity, ProfileEditSama, ProfileEditUniversity, ProfileEditDegree;
    private Button ProfileEditUpdateButton , ProfileEditUserImageButton;

    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private DatabaseReference EditProfileRef , PostsRef;
    private StorageReference UserProfileImageRef;

    final static int Gallery_Pick =  1;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        RelatedFirebase();
        SettingToolbar();
        Initialize();
        RetrieveUserInfo();

        ProfileEditUserImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });



        ProfileEditUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileInfo();
            }
        });


    }

    private void updateProfileInfo()
    {
        String username     =   ProfileEditUserName.getText().toString();
        String job          =   ProfileEditJob.getText().toString();
        String city         =   ProfileEditCity.getText().toString();
        String sama         =   ProfileEditSama.getText().toString();
        String university   =   ProfileEditUniversity.getText().toString();
        String degree       =   ProfileEditDegree.getText().toString();



        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(job)||TextUtils.isEmpty(city)||TextUtils.isEmpty(sama)||TextUtils.isEmpty(university)||TextUtils.isEmpty(degree))
        {
            Toast.makeText(this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Updating");
            loadingBar.setMessage("Please wait, while we are uploading your new Account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            UpdateAccountInfo(username,job,city,sama,university,degree);
        }

    }

    private void UpdateAccountInfo(final String username, final String job, final String city, String sama, String university, String degree)
    {
        HashMap userMap = new HashMap();

        userMap.put("A_name", username);
        userMap.put("B_job", job);
        userMap.put("C_city", city);
        userMap.put("D_samaNumber", sama);
        userMap.put("E_university", university);
        userMap.put("G_degree", degree);

        EditProfileRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful())
                {
                    Query myPostsQuery = PostsRef.orderByChild("uid")
                            .startAt(CurrentUserID).endAt(CurrentUserID + "\uf8ff");

                    HashMap postMap = new HashMap();

                    postMap.put("username",username);
                    postMap.put("job",job);
                    postMap.put("city",city);

                    myPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren())
                            {
                                snapshot.getRef().child("username").setValue(username);
                                snapshot.getRef().child("job").setValue(job);
                                snapshot.getRef().child("city").setValue(city);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    Toast.makeText(ProfileEditActivity.this, "Account settings has been updated Successfully", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(ProfileEditActivity.this, "Error Occurred, updating account information ...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }



            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ProfileEditActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void RelatedFirebase()
    {
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        EditProfileRef =FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    private void SettingToolbar()
    {
        mToolbar  = (Toolbar) findViewById(R.id.profile_edit_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void Initialize()
    {
        loadingBar = new ProgressDialog(this);


        ProfileEditImageView = (ImageView) findViewById(R.id.profile_edit_user_image);
        ProfileEditUserName  = (EditText)  findViewById(R.id.profile_edit_UserName);
        ProfileEditJob       = (EditText)  findViewById(R.id.profile_edit_Job);
        ProfileEditCity      = (EditText)  findViewById(R.id.profile_edit_City);
        ProfileEditSama      = (EditText)  findViewById(R.id.profile_edit_sama);
        ProfileEditUniversity= (EditText)  findViewById(R.id.profile_edit_university);
        ProfileEditDegree    = (EditText)  findViewById(R.id.profile_edit_degree);
        ProfileEditUpdateButton     = (Button) findViewById(R.id.profile_edit_update_btn);
        ProfileEditUserImageButton  = (Button) findViewById(R.id.profile_edit_user_image_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUri = data.getData();


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are uploading your new Account...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                final Uri resultUri = result.getUri();
                StorageReference filePath = UserProfileImageRef.child(CurrentUserID+".jpg");


                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                final String download_url = uri.toString();
                                EditProfileRef.child("profileimage").setValue(download_url);

                                Picasso.with(ProfileEditActivity.this).load(download_url).placeholder(R.drawable.profile).into(ProfileEditImageView);
                                Toast.makeText(ProfileEditActivity.this, "Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }


                        });

                    }
                });



            }

            else
            {
                Toast.makeText(this, "Error Occurred : Image cannot be cropped. TryAgain!"  , Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void RetrieveUserInfo()
    {
        EditProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("A_name"))
                    {
                        String myUserName = dataSnapshot.child("A_name").getValue().toString();
                        ProfileEditUserName.setText(myUserName);
                    }
                    if(dataSnapshot.hasChild("B_job"))
                    {
                        String myUserJob = dataSnapshot.child("B_job").getValue().toString();
                        ProfileEditJob.setText(myUserJob);
                    }
                    if(dataSnapshot.hasChild("C_city"))
                    {
                        String myUserCity = dataSnapshot.child("C_city").getValue().toString();
                        ProfileEditCity.setText(myUserCity);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(ProfileEditActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(ProfileEditImageView);

                    }
                    if(dataSnapshot.hasChild("D_samaNumber"))
                    {
                        String myUserSama = dataSnapshot.child("D_samaNumber").getValue().toString();
                        ProfileEditSama.setText(myUserSama);
                    }
                    if(dataSnapshot.hasChild("E_university"))
                    {
                        String myUserUniversity = dataSnapshot.child("E_university").getValue().toString();
                        ProfileEditUniversity.setText(myUserUniversity);
                    }
                    if(dataSnapshot.hasChild("G_degree"))
                    {
                        String myUserDegree = dataSnapshot.child("G_degree").getValue().toString();
                        ProfileEditDegree.setText(myUserDegree);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

}
