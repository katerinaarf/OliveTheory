package com.example.olivetheory;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignUp, btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RadioGroup userTypeGroup;
    private RadioButton farmerRadioButton, expertRadioButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_up);

        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputEmail = findViewById(R.id.emailEditText);
        inputPassword = findViewById(R.id.passwordEditText);
        inputName = findViewById(R.id.nameEditText);
        userTypeGroup = findViewById(R.id.userTypeRadioGroup);
        farmerRadioButton = findViewById(R.id.farmerRadioButton);
        expertRadioButton = findViewById(R.id.expertRadioButton);
        btnSignUp = findViewById(R.id.signUpButton);
        btnLogin = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                int selectedUserTypeId = userTypeGroup.getCheckedRadioButtonId();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Εισάγετε το email σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Εισάγετε τον κωδικό σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Πολύ μικρός κωδικός, εισάγετε τουλάχιστον 6 χαρακτήρες!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Εισάγετε το όνομά σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedUserTypeId == -1) {
                    Toast.makeText(getApplicationContext(), "Επιλέξτε τον τύπο χρήστη!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Convert the selected radio button ID to user type
                String userType;
                if (selectedUserTypeId == R.id.farmerRadioButton) {
                    userType = "Αγρότης";
                } else if (selectedUserTypeId == R.id.expertRadioButton) {
                    userType = "Γεωπόνος";
                } else {
                    // This should never happen, but if it does, handle it accordingly
                    userType = "Unknown";
                }


                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Create user profile in Firestore
                                    String userId = auth.getCurrentUser().getUid();
                                    saveUserInfo(name, email, userType, userId);
                                } else {
                                    // Handle signup failure
                                }
                            }
                        });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Εισάγετε το email σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Εισάγετε τον κωδικό σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(SignUpActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void saveUserInfo(String name, String email, String userType, String userId) {
        User currentUser = new User(name, email, userType, userId);
        // Save user information to Firestore
        db.collection("users").document(userId)
                .set(currentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Profile created successfully, redirect to MapsActivity
                        Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle profile creation failure
                    }
                });
    }
}