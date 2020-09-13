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

import de.hdodenhof.circleimageview.CircleImageView;

public class EmptyCartActivity extends AppCompatActivity {

    Button buttonAddToCart;
    ImageView bck;
    CircleImageView nav1, nav2, nav3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_cart);

        bck=findViewById(R.id.bck_btn);
        nav1 = findViewById(R.id.nav1);
        nav2 = findViewById(R.id.nav2);
        nav3 = findViewById(R.id.nav3);

        nav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmptyCartActivity.this, "Fill your cart before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

        nav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EmptyCartActivity.this, "Fill your cart before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });

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