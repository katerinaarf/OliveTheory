package com.example.olivetheory;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    TextInputEditText nameEditText, emailEditText, passwordEditText;
    Button btnSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textview;
    private RadioGroup userTypeGroup;
    private RadioButton farmerRadioButton, expertRadioButton;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(SignUpActivity.this, LogOutActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_up);
        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        btnSignUp = findViewById(R.id.signUpButton);
        userTypeGroup = findViewById(R.id.userTypeRadioGroup);
        farmerRadioButton = findViewById(R.id.farmerRadioButton);
        expertRadioButton = findViewById(R.id.expertRadioButton);
        progressBar = findViewById(R.id.progresBar);
        textview = findViewById(R.id.loginNow);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name, email, password;
                name = String.valueOf(nameEditText.getText());
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                int type = userTypeGroup.getCheckedRadioButtonId();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε το όνομά σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε το email σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε τον κωδικό σας!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Πολύ μικρός κωδικός, εισάγετε τουλάχιστον 6 χαρακτήρες!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (type == -1) {
                    Toast.makeText(getApplicationContext(), "Επιλέξτε τον τύπο χρήστη!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert the selected radio button ID to user type
                String userType;
                if (type == R.id.farmerRadioButton) {
                    userType = "Αγρότης";
                } else if (type == R.id.expertRadioButton) {
                    userType = "Γεωπόνος";
                } else {
                    // This should never happen, but if it does, handle it accordingly
                    userType = "Unknown";
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        // Save additional user data to database
                                        User user = new User(name, email, password, userType);
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                                        userRef.setValue(user);

                                        Toast.makeText(SignUpActivity.this, "Επιτυχής εγγραφή χρήστη!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Ανεπιτυχής εγγραφή χρήστη.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}