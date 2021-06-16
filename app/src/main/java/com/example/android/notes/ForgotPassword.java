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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText mForgotPasswordEmail;
    private Button mResetPassword;
    private TextView mGoToLogin;

    private FirebaseAuth firebaseAuth;

    private ProgressBar mForgotPasswordProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();

        mForgotPasswordEmail = findViewById(R.id.forgot_password_email);
        mResetPassword = findViewById(R.id.reset_password);
        mGoToLogin = findViewById(R.id.go_to_login);
        mForgotPasswordProgressBar = findViewById(R.id.forgot_password_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finishActivity(100);
                Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mForgotPasswordEmail.getText().toString().trim();
                if(email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();
                }
                else {
                    // We have to send password recover email
                    mForgotPasswordProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "You can recover your password through mail sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Email is incorrect or Account does not exist", Toast.LENGTH_SHORT).show();
                                mForgotPasswordProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
        finish();
    }
}