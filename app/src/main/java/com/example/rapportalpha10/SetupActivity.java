package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    String currentUserID;
    private DatabaseReference userRef;
    private StorageReference UserProfileImageRef;



    private CircleImageView SetupProfileImage;
    private TextView addProfileImageText;

    private EditText name;
    private EditText job;
    private EditText city;

    private EditText samaNumber;
    private Spinner universityList;

    private TextView date_of_graduation;
    private TextView degree;

    private Button continue_button;

    private ProgressDialog loadingBar;



    String universityValue;
    final static int Gallery_Pick =  1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


            UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        SetupProfileImage = findViewById(R.id.setup_profile_Image);
        addProfileImageText = findViewById(R.id.setup_add_profile_image);



        name = findViewById(R.id.setup_name);
        job = findViewById(R.id.setup_job);
        city = findViewById(R.id.setup_city);

        samaNumber = findViewById(R.id.setup_samaNumber);

        date_of_graduation = findViewById(R.id.setup_date_of_graduation);
        degree = findViewById(R.id.setup_degree);

        continue_button = findViewById(R.id.setup_continue_btn);

        loadingBar = new ProgressDialog(this);


        universityList = (Spinner) findViewById(R.id.setup_university_list);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.MedicalUni , android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        universityList.setAdapter(adapter1);


        universityList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position).equals("Select Your University"))
                {

                }
                else
                {
                    universityValue = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });


        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.hasChild("profileimage"))
                        {

                            StorageReference filePath = UserProfileImageRef.child(currentUserID+".jpg");
                            Uri imageUri = Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/"+R.drawable.profile);
                            UploadTask uploadTask;

                            uploadTask = filePath.putFile(imageUri);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {
                                            final String download_url = uri.toString();
                                            userRef.child("profileimage").setValue(download_url);

                                            Picasso.with(SetupActivity.this).load(download_url).placeholder(R.drawable.profile).into(SetupProfileImage);
                                            Toast.makeText(SetupActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                        }


                                    });

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                SaveAccountSetupInformation();

            }
        });

        SetupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
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
                StorageReference filePath = UserProfileImageRef.child(currentUserID+".jpg");


                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                final String download_url = uri.toString();
                                userRef.child("profileimage").setValue(download_url);

                                Picasso.with(SetupActivity.this).load(download_url).placeholder(R.drawable.profile).into(SetupProfileImage);
                                Toast.makeText(SetupActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }


                        });

                    }
                });



            }

            else
            {
                Toast.makeText(this, "Error Occured : Image cannot be croped. TryAgain!"  , Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetupInformation()
    {
        String username = name.getText().toString();
        String jobOccupation = job.getText().toString();
        String cityplace = city.getText().toString();

        String sama = samaNumber.getText().toString();

        String uni = universityValue;

        String date_of_grad = date_of_graduation.getText().toString();
        String mdegree = degree.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(jobOccupation))
        {
            Toast.makeText(this, "Please Enter Your Job or Occupation", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(cityplace))
        {
            Toast.makeText(this, "Please Enter Your City", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(sama))
        {
            Toast.makeText(this, "Please Enter Your Sama", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            HashMap userMap = new HashMap();
            userMap.put("A_name",username);
            userMap.put("B_job",jobOccupation);
            userMap.put("C_city",cityplace);
            userMap.put("D_samaNumber",sama);
            userMap.put("E_university",uni);
            userMap.put("F_dateOfGraduation","not setup yet");
            userMap.put("G_degree",mdegree);

            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {

            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
    }






}
