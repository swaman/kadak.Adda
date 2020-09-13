package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShippingAddressActivity extends AppCompatActivity {
    EditText pincodeEditText;
    EditText houseNoEditText;
    EditText colonyEditText;
    EditText cityEditText;
    EditText stateEditText;
    EditText landmarkEditText;
    EditText nameEditText;
    EditText mobileNoEditText;
    Button saveButton, backButton;

    String value;

    String uid;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore mfirestore;

    Map<String , ArrayList<String>> map;
    ArrayList<String> addressMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mfirestore = FirebaseFirestore.getInstance();
        map = new HashMap<>();
        addressMap = new ArrayList<>();

        pincodeEditText =findViewById(R.id.pincode);
        houseNoEditText =findViewById(R.id.houseno);
        colonyEditText = findViewById(R.id.roadno);
        cityEditText = findViewById(R.id.city_input);
        stateEditText= findViewById(R.id.state_input);
        landmarkEditText=findViewById(R.id.landmark);
        nameEditText = findViewById(R.id.name);
        mobileNoEditText= findViewById(R.id.mobile);
        saveButton =findViewById(R.id.save_address);
        backButton = findViewById(R.id.back_arrow);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        value = bundle.getString("value");
        Boolean isPrevPresent = bundle.getBoolean("PrevPresent");
        if(isPrevPresent) {
            Gson gson = new Gson();
            String prevAdd = bundle.getString("PreviousAddresses");
            java.lang.reflect.Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            addressMap = gson.fromJson(prevAdd, type);
        }
        uid = currentUser.getUid();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void saveAddress(){
        String pincode =pincodeEditText.getText().toString();
        String houseNo =houseNoEditText.getText().toString();
        String colony =colonyEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String landmark =landmarkEditText.getText().toString();
        String fullAddress = pincode+", "+houseNo+", "+colony+", "+landmark+", "+city+", "+state;

        if (pincode.equals("")||houseNo.equals("")||colony.equals("")||city.equals("")||state.equals("")){
            Toast.makeText(this, "Please fill all the blocks", Toast.LENGTH_SHORT).show();
        }else{
            saveButton.setEnabled(false);
            addressMap.add(fullAddress);
            if(value.equals("3"))
                addressMap.remove(0);

            map.put("addresses", addressMap);

            mfirestore
                    .collection("user")
                    .document(uid)
                    .update("pDetails.addresses", addressMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ShippingAddressActivity.this, "Address Added", Toast.LENGTH_SHORT).show();
                            if(!value.equals("3"))
                                value = String.valueOf(Integer.parseInt(value) + 1);
                            saveButton.setEnabled(true);
                            sendUserBack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ShippingAddressActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            saveButton.setEnabled(true);
                        }
                    });

        }
    }

    private void sendUserBack() {
        Intent intent = new Intent(ShippingAddressActivity.this, AddressOptionActivity.class);
        if(value.equals("0")){
            intent = new Intent(ShippingAddressActivity.this, AddressEmptyActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        sendUserBack();
    }
}