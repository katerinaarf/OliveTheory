package com.example.olivetheory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.olivetheory.User;

public class SignUpActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    RadioButton farmerRadioButton, expertRadioButton;
    Button signUpButton, loginButton;
    boolean isSigningUp = true;
    UserDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        farmerRadioButton = findViewById(R.id.farmerRadioButton);
        expertRadioButton = findViewById(R.id.expertRadioButton);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);

        db = new UserDatabaseHelper(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSigningUp) {
                    signUp();
                } else {
                    login();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSignUpLogin();
            }
        });
    }

    private void toggleSignUpLogin() {
        if (isSigningUp) {
            isSigningUp = false;
            signUpButton.setText("Σύνδεση");
            loginButton.setText("Εγγραφή");
            nameEditText.setVisibility(View.GONE);
            farmerRadioButton.setVisibility(View.GONE);
            expertRadioButton.setVisibility(View.GONE);
        } else {
            isSigningUp = true;
            signUpButton.setText("Εγγραφή");
            loginButton.setText("Σύνδεση");
            nameEditText.setVisibility(View.VISIBLE);
            farmerRadioButton.setVisibility(View.VISIBLE);
            expertRadioButton.setVisibility(View.VISIBLE);
        }
    }

    private void signUp() {
        // Get user inputs
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String userType = "";
        if (farmerRadioButton.isChecked()) {
            userType = "Farmer";
        } else if (expertRadioButton.isChecked()) {
            userType = "Expert";
        }

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || userType.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία.", Toast.LENGTH_SHORT).show();
        } else {
            // Check if user already exists in the database
            if (db.checkUser(email)) {
                Toast.makeText(this, "Αυτός ο χρήστης υπάρχει ήδη.Παρακαλώ συνδεθείτε.", Toast.LENGTH_SHORT).show();
            } else {
                // Save user profile to the database
                User user = new User(0, name, email, password, userType);
                db.addUser(user);
                Toast.makeText(this, "Ο χρήστης εγγράφηκε επιτυχώς.", Toast.LENGTH_SHORT).show();

                // Navigate to MenuActivity
                Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        }
    }

    private void login() {
        // Get user inputs
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία.", Toast.LENGTH_SHORT).show();
        } else {
            // Check if user exists in the database
            if (db.checkUser(email, password)) {
                Toast.makeText(this, "Ο χρήστης συνδέθηκε επιτυχώς.", Toast.LENGTH_SHORT).show();
                // Navigate to MenuActivity
                Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Λανθασμένο email ή κωδικός.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
