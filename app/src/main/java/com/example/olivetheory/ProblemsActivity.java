package com.example.olivetheory;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
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
            InputStream is = getAssets().open("diseases.json");
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
