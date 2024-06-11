package com.example.olivetheory.gui.mainactivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.olivetheory.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageView back = findViewById(R.id.back_button);
        ImageView linkedinButtonvas = findViewById(R.id.linkedinvas_button);
        ImageView linkedinButtonkat = findViewById(R.id.linkedinkat_button);
        linkedinButtonvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "www.linkedin.com/in/vasiliki-liakou-20924a307";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        linkedinButtonkat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "www.linkedin.com/in/katerina-arfani-4267402b8";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(InfoActivity.this, MenuActivity.class);
                startActivity(back);
            }
        });
    }
}
