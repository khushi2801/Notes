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

public class MainActivity extends AppCompatActivity {

    private EditText mLoginEmail, mLoginPassword;
    private Button mLogin;
    private TextView mGoToForgotPassword, mGoToSignUp;

    private FirebaseAuth firebaseAuth;

    private ProgressBar mMainProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);
        mGoToForgotPassword = findViewById(R.id.go_to_forgot_password);
        mLogin = findViewById(R.id.login);
        mGoToSignUp = findViewById(R.id.go_to_sign_up);
        mMainProgressBar = findViewById(R.id.main_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            finish();
            startActivity(new Intent(MainActivity.this, Notes.class));
        }

        mGoToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        mGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });



        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString().trim();
                String password = mLoginPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Register the user to firebase
                    mMainProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                checkEmailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                                mMainProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified() == true) {
            Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, Notes.class));
        }
        else {
            mMainProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Verify your email first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}