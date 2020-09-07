package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    TextView textViewContactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        textViewContactUs = findViewById(R.id.textView2);
        textViewContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go Back
                startActivity(new Intent(getApplicationContext(),NavigaActivity.class));
                finish();
            }
        });
    }
}