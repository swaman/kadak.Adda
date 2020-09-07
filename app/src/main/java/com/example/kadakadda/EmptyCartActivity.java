package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class EmptyCartActivity extends AppCompatActivity {

    Button buttonAddToCart;
    ImageView bck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        bck=findViewById(R.id.bck_btn);

        buttonAddToCart = findViewById(R.id.addToCart);
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmptyCartActivity.this, NavigaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),NavigaActivity.class));
                finish();
            }
        });


    }


}