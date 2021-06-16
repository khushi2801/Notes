package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private EditText mUserEmail, mUserPassword;
    private Button mCreateAccount;
    private TextView mGoToLogin;

    private FirebaseAuth firebaseAuth;

    private ProgressBar mSignUpProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        mUserEmail = findViewById(R.id.new_user_email);
        mUserPassword = findViewById(R.id.new_user_password);
        mCreateAccount = findViewById(R.id.create_account);
        mGoToLogin = findViewById(R.id.go_to_login);
        mSignUpProgressBar = findViewById(R.id.sign_up_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUserEmail.getText().toString().trim();
                String password = mUserPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<7) {
                    Toast.makeText(getApplicationContext(), "Enter password greater than 7 digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Register the user to firebase
                    mSignUpProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed to register, please try again", Toast.LENGTH_SHORT).show();
                                mSignUpProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    // Send email verification
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "A verification email is send, verify and login again", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SignUp.this, MainActivity.class));
                    finish();
                }
            });
        }
        else {
            mSignUpProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp.this, MainActivity.class));
        finish();
    }
}