package com.example.olivetheory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class ProblemsActivity extends AppCompatActivity {

    private EditText answerView;
    private TextView resultView, treatment;
    private ImageView diseaseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // In the onCreate method of an activity that should only be accessible to farmers
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userType = document.getString("userType");
                                    if (!"Αγρότης".equals(userType)) {
                                        Toast.makeText(ProblemsActivity.this, "Η πρόσβαση σε αυτή τη σελίδα επιτρέπεται μόνο σε αγρότες.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }
                    });
        }

        setContentView(R.layout.activity_problems);

        answerView = findViewById(R.id.answer_view);
        resultView = findViewById(R.id.result_view);
        diseaseImage = findViewById(R.id.disease_image);
        treatment = findViewById(R.id.treatment);
        Button postButton = findViewById(R.id.post_button);
        Button back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = answerView.getText().toString().toLowerCase();
                String json = loadJSONFromAsset();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Disease>>() {}.getType();
                List<Disease> diseases = gson.fromJson(json, listType);
                boolean found = false;

                for (Disease disease : diseases) {
                    for (String keyword : disease.getKeywords()) {
                        if (userInput.contains(keyword.toLowerCase())) {
                            resultView.setText(disease.getName() );
                            treatment.setText("Αντιμετώπιση: " + disease.getTreatment());

                            int resourceId = getResources().getIdentifier(disease.getImage(), "drawable", getPackageName());
                            Glide.with(ProblemsActivity.this).load(resourceId).into(diseaseImage);

                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

                if (!found) {
                    resultView.setText("Δεν βρέθηκε αντιστοιχία για το πρόβλημά σας.");
                    diseaseImage.setImageResource(0); // Καθαρισμός της εικόνας
                }
            }
        });
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("disease.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }}
