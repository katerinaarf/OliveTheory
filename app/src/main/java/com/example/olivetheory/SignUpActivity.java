package com.example.olivetheory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    // Define REQUEST_CODE_MAP constant
    private static final int REQUEST_CODE_MAP = 101;

    EditText nameEditText, emailEditText, passwordEditText;
    RadioButton farmerRadioButton, expertRadioButton;
    Button signUpButton, loginButton;
    boolean isSigningUp = true;
    UserDatabaseHelper db;

    // Variables to store selected place details
    double latitude, longitude;
    String placeName;

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
                User user = new User(name, email, password, userType);
                // Set selected place details
                user.setLatitude(latitude);
                user.setLongitude(longitude);
                user.setPlaceName(placeName);

                // Start MapsActivity to select location
                Intent mapsIntent = new Intent(SignUpActivity.this, MenuActivity.class);
                startActivityForResult(mapsIntent, REQUEST_CODE_MAP);
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
                String userType = db.getUserType(email);
                Toast.makeText(this, "Ο χρήστης συνδέθηκε επιτυχώς.", Toast.LENGTH_SHORT).show();
                // Show the selection popup and navigate to MapsActivity
                showMapSelectionPopup(userType);
            } else {
                Toast.makeText(this, "Λανθασμένο email ή κωδικός.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //pop up message for every user
    private void showMapSelectionPopup(String userType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (userType.equals("Farmer")) {
            builder.setMessage("Παρακαλώ επιλέξτε το μέρος στο οποίο έχετε κάποια γεωγραφική καλλιεργίσιμη έκταση.");
        } else if (userType.equals("Expert")) {
            builder.setMessage("Παρακαλώ επιλέξτε την τοποθεσία που βρίσκεται το γραφείο σας.");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Navigate to MapsActivity for selecting the place
                Intent maps = new Intent(SignUpActivity.this, MenuActivity.class);
                startActivityForResult(maps, REQUEST_CODE_MAP);
            }
        });
        builder.create().show();
    }

    // Method to receive selected place details from MapsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            // Create a user with the selected location
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String userType = farmerRadioButton.isChecked() ? "Farmer" : "Expert";

            User user = new User(name, email, password, userType);
            user.setLatitude(latitude);
            user.setLongitude(longitude);

            // Save user to the database
            db.addUser(user);
            Toast.makeText(this, "Ο χρήστης εγγράφηκε επιτυχώς.", Toast.LENGTH_SHORT).show();

            // Proceed to the next activity or finish sign-up process
        }
    }



}
