package com.example.olivetheory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ImageView displayimage = findViewById(R.id.displayimage);
        ImageButton back = findViewById(R.id.back);
        TextView buttonChangeImage = findViewById(R.id.buttonChangeImage);
        TextView buttonChangeName = findViewById(R.id.buttonChangeName);
        TextView buttonChangeEmail = findViewById(R.id.buttonChangeEmail);
        TextView buttonChangePassword = findViewById(R.id.buttonChangePassword);
        TextView buttonLogout = findViewById(R.id.buttonLogout);
        TextView buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);

        buttonChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserName();
            }
        });

        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserEmail();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(MenuActivity.class);
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setMessage("Ανάκτηση Κωδικού;");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = resetPassword.getText().toString();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UserProfile.this, "Επιτυχής ανάκτηση κωδικού!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserProfile.this, "Αδύνατη ανάκτηση κωδικού", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                passwordResetDialog.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });


        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startNewActivity(LogOutActivity.class);
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
        });
    }

    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(UserProfile.this, cls);
        startActivity(intent);
    }

    private void changeUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Αλλαγή Ονόματος Χρήστη");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Αλλαγή", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUserName = input.getText().toString();
                if (!newUserName.isEmpty()) {
                    // Update the user's display name in Firebase Authentication
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUserName)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // If Authentication update is successful, update Firestore
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    String userId = user.getUid(); // Get the user ID

                                    // Create a map with the new user name
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("name", newUserName);

                                    // Update the Firestore document
                                    db.collection("users").document(userId)
                                            .update(updates)
                                            .addOnCompleteListener(firestoreTask -> {
                                                if (firestoreTask.isSuccessful()) {
                                                    Toast.makeText(UserProfile.this, "Το όνομα χρήστη άλλαξε επιτυχώς.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UserProfile.this, "Αποτυχία αλλαγής ονόματος χρήστη στο Firestore.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(UserProfile.this, "Αποτυχία αλλαγής ονόματος χρήστη.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(UserProfile.this, "Το όνομα χρήστη δεν μπορεί να είναι κενό.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void changeUserEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Αλλαγή Email");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Αλλαγή", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = input.getText().toString();
                if (!newEmail.isEmpty()) {
                    // Prompt for the user's current password for reauthentication
                    AlertDialog.Builder passwordBuilder = new AlertDialog.Builder(UserProfile.this);
                    passwordBuilder.setTitle("Εισάγετε τον τρέχοντα κωδικό σας");

                    final EditText passwordInput = new EditText(UserProfile.this);
                    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordBuilder.setView(passwordInput);

                    passwordBuilder.setPositiveButton("Επαλήθευση", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String currentPassword = passwordInput.getText().toString();
                            if (!currentPassword.isEmpty()) {
                                // Reauthenticate the user
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                                user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                                    if (reauthTask.isSuccessful()) {
                                        Log.d("Reauth", "Reauthentication successful");

                                        // Send verification email to the new email address
                                        FirebaseAuth.getInstance().getCurrentUser().verifyBeforeUpdateEmail(newEmail)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Log.d("Email Update", "Verification email sent to new email address");

                                                        // Prompt the user to check their new email and confirm the verification
                                                        Toast.makeText(UserProfile.this, "Ένα email επαλήθευσης έχει σταλεί στο νέο email. Παρακαλώ επιβεβαιώστε την αλλαγή.", Toast.LENGTH_LONG).show();

                                                        // Listen for changes to the user's email verified status (optional but recommended)
                                                        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
                                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                                            if (user != null && user.isEmailVerified()) {
                                                                Log.d("Email Verification", "New email address has been verified");

                                                                // Update the email in Firestore after verification
                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                String userId = user.getUid();

                                                                // Create a map with the new email
                                                                Map<String, Object> updates = new HashMap<>();
                                                                updates.put("email", newEmail);

                                                                // Update the Firestore document
                                                                db.collection("users").document(userId)
                                                                        .update(updates)
                                                                        .addOnCompleteListener(firestoreTask -> {
                                                                            if (firestoreTask.isSuccessful()) {
                                                                                Log.d("Firestore Update", "Firestore update successful");
                                                                                Toast.makeText(UserProfile.this, "Το email άλλαξε επιτυχώς.", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Exception e = firestoreTask.getException();
                                                                                String message = e != null ? e.getMessage() : "Unknown error";
                                                                                Log.e("Firestore Update", "Error updating email in Firestore: " + message);
                                                                                Toast.makeText(UserProfile.this, "Αποτυχία αλλαγής email στο Firestore: " + message, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        });

                                                    } else {
                                                        Exception e = task.getException();
                                                        String message = e != null ? e.getMessage() : "Unknown error";
                                                        Log.e("Email Update", "Error sending verification email: " + message);
                                                        Toast.makeText(UserProfile.this, "Αποτυχία αποστολής email επαλήθευσης: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Exception e = reauthTask.getException();
                                        String message = e != null ? e.getMessage() : "Unknown error";
                                        Log.e("Reauthentication", "Error during reauthentication: " + message);
                                        Toast.makeText(UserProfile.this, "Η επαλήθευση απέτυχε: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(UserProfile.this, "Ο κωδικός δεν μπορεί να είναι κενός.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    passwordBuilder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    passwordBuilder.show();
                } else {
                    Toast.makeText(UserProfile.this, "Το email δεν μπορεί να είναι κενό.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    private void deleteUserAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Διαγραφή Λογαριασμού");
        builder.setMessage("Είστε σίγουροι ότι θέλετε να διαγράψετε τον λογαριασμό σας; Αυτή η ενέργεια δεν μπορεί να αναιρεθεί.");

        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, "Ο λογαριασμός διαγράφηκε επιτυχώς.", Toast.LENGTH_SHORT).show();
                                startNewActivity(LogOutActivity.class);
                                finish();
                            } else {
                                Toast.makeText(UserProfile.this, "Αποτυχία διαγραφής λογαριασμού.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
