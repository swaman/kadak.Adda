package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AddressEmptyActivity extends AppCompatActivity {

    Button buttonAdd, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_empty);
        buttonAdd = findViewById(R.id.addAddress);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressEmptyActivity.this, ShippingAddressActivity.class);
                intent.putExtra("value", "0");
                intent.putExtra("PrevPresent", false);
                startActivity(intent);
            }
        });

        backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}