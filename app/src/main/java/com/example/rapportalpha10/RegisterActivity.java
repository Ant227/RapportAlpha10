package com.example.rapportalpha10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_password_confirm);
        CreateAccountBtn = (Button) findViewById(R.id.register_continue);
        loadingBar = new ProgressDialog(this);


        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }


        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }



    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {

            Toast.makeText(RegisterActivity.this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password))
        {

            Toast.makeText(RegisterActivity.this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confirmPassword))
        {

            Toast.makeText(RegisterActivity.this, "Please Confirm Your Password...", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(confirmPassword))
        {
            Toast.makeText(RegisterActivity.this, "Your password do not match with your confirm password", Toast.LENGTH_SHORT).show();

        } else
            {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this, "You are authenticated successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }

                        private void sendUserToSetupActivity() {
                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(setupIntent);
                            finish();

                        }
                    });

        }
    }
}
