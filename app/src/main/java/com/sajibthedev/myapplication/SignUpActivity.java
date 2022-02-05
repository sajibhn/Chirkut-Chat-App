package com.sajibthedev.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sajibthedev.myapplication.Models.Users;
import com.sajibthedev.myapplication.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("we are creating your account");

        // go to sign in activity
        binding.alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Register User

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etUserName.getText().toString().isEmpty()){
                    binding.etUserName.setError("Enter your username");
                    return;
                }

                if (binding.etEmail.getText().toString().isEmpty()){
                    binding.etEmail.setError("Enter your email");
                    return;
                }
                if (binding.etPassword.getText().toString().isEmpty()){
                    binding.etPassword.setError("Enter your password");
                    return;
                }
                if (binding.etPassword.getText().toString().length() <6){
                    binding.etPassword.setError("Min character is 6");
                    return;
                }
                progressDialog.show();

                // AUTHENTICATION

                auth.createUserWithEmailAndPassword(
                        binding.etEmail.getText().toString(),
                        binding.etPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Users user = new Users(
                                            binding.etUserName.getText().toString(),
                                            binding.etEmail.getText().toString(),
                                            binding.etPassword.getText().toString()
                                    );

                                    String id = task.getResult().getUser().getUid();

                                    database.getReference().child("Users").child(id).setValue(user);

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);

                                    Toast.makeText(SignUpActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Registration is failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }
}