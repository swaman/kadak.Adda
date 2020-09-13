package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddressEmptyActivity extends AppCompatActivity {

    Button buttonAdd, backButton;
    CircleImageView nav1, nav2, nav3;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_empty);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        buttonAdd = findViewById(R.id.addAddress);
        nav1 = findViewById(R.id.nav1);
        nav2 = findViewById(R.id.nav2);
        nav3 = findViewById(R.id.nav3);

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

        nav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartEntry();
            }
        });

        nav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddressEmptyActivity.this, "Add an address to proceed ahead.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cartEntry() {
        //progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("user").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        //progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Map<String, Object> map = task.getResult().getData();
                            Map<String, Object> cart = (Map<String, Object>) map.get("cart");
                            if(cart == null || cart.isEmpty()){
                                //TODO : Navigate to empty cart activity
                                Intent intent = new Intent(AddressEmptyActivity.this, EmptyCartActivity.class);
                                startActivity(intent);
                            }
                            else{
                                //open Cart
                                Intent intent = new Intent(AddressEmptyActivity.this, CartActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(AddressEmptyActivity.this, "There was some error." + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}