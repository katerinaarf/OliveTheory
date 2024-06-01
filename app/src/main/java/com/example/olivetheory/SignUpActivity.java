package com.example.olivetheory;

import static android.content.ContentValues.TAG;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity {
    EditText nameEditText, emailEditText, passwordEditText;
    Button btnSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textview;
    private RadioGroup userTypeGroup;
    private RadioButton farmerRadioButton, expertRadioButton;

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
        progressBar = findViewById(R.id.progresBar); // Ensure the ID is correct
        textview = findViewById(R.id.loginNow);

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                int type = userTypeGroup.getCheckedRadioButtonId();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε το όνομά σας!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε το email σας!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Εισάγετε τον κωδικό σας!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Πολύ μικρός κωδικός, εισάγετε τουλάχιστον 6 χαρακτήρες!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (type == -1) {
                    Toast.makeText(getApplicationContext(), "Επιλέξτε τον τύπο χρήστη!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                String userType;
                if (type == R.id.farmerRadioButton) {
                    userType = "Αγρότης";
                } else if (type == R.id.expertRadioButton) {
                    userType = "Γεωπόνος";
                } else {
                    userType = "Unknown";
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").whereEqualTo("name", name).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "Το όνομα υπάρχει ήδη, επιλέξτε ένα διαφορετικό όνομα!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        progressBar.setVisibility(View.GONE);
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                            if (firebaseUser != null) {
                                                                String userid = firebaseUser.getUid();
                                                                User user = new User(name, email, userType, userid);
                                                                db.collection("users")
                                                                        .document(userid)
                                                                        .set(user)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(SignUpActivity.this, "Επιτυχής εγγραφή χρήστη!", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                } else {
                                                                                    Log.e(TAG, "User registration failed: " + task.getException().getMessage());
                                                                                    Toast.makeText(SignUpActivity.this, "Ανεπιτυχής εγγραφή χρήστη.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            Log.e(TAG, "Authentication failed: " + task.getException().getMessage());
                                                            Toast.makeText(SignUpActivity.this, "Ανεπιτυχής εγγραφή χρήστη.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.e(TAG, "Name check failed: " + task.getException().getMessage());
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUpActivity.this, "Σφάλμα κατά τον έλεγχο του ονόματος.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
