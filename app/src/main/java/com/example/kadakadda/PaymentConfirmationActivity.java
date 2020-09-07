package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentConfirmationActivity extends AppCompatActivity {

    Button backToHome;
    Button goToMyOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);
        backToHome = findViewById(R.id.back_to_home);
        goToMyOrders = findViewById(R.id.review_order);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToHome = new Intent(PaymentConfirmationActivity.this, NavigaActivity.class);
                goToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                goToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToHome);
            }
        });
        goToMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToOrders = new Intent(PaymentConfirmationActivity.this, MyOrders.class);
                goToOrders.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                goToOrders.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToOrders);
            }
        });

    }


}