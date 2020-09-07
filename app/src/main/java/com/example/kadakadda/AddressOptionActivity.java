package com.example.kadakadda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class AddressOptionActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton radioButton, add_1, add_2, add_3;
    Button backButton;
    ProgressBar progressBar;

    FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid, add1, add2, add3, value;
    ArrayList<String> Address;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_option);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        mfirestore = FirebaseFirestore.getInstance();
        docRef = mfirestore.collection("user").document(uid);

        radioGroup = findViewById(R.id.addressOption);
        add_1 = findViewById(R.id.add_1);
        add_2 = findViewById(R.id.add_2);
        add_3 = findViewById(R.id.add_3);
        backButton = findViewById(R.id.back_arrow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progress_bar);

        add_1.setVisibility(View.GONE);
        add_2.setVisibility(View.GONE);
        add_3.setVisibility(View.GONE);
    }
    public void addNewAddress(View view){
        Intent shippingAddress = new Intent(AddressOptionActivity.this, ShippingAddressActivity.class);
        shippingAddress.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shippingAddress.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shippingAddress.putExtra("value",value);
        Boolean isPrevPresent = (Address.size()!=0);
        shippingAddress.putExtra("PrevPresent", isPrevPresent);
        if(isPrevPresent){
            Gson gson = new Gson();
            String prevAddresses = gson.toJson(Address);
            shippingAddress.putExtra("PreviousAddresses", prevAddresses);
        }
        startActivity(shippingAddress);
    }
    public void checkedButton(View view){
         int radioId = radioGroup.getCheckedRadioButtonId();
         radioButton = findViewById(radioId);
         String address = radioButton.getText().toString();
        editor.putString(getString(R.string.selectedAddress), address);
        editor.commit();
        //proceed for payment
        Intent intent = new Intent(this, PaymentMessageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        Address = new ArrayList<>();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getData().get("pDetails")!=null){
                            Map<String, Object> pDetails = (Map<String, Object>) document.getData().get("pDetails");
                            Address = (ArrayList<String>) pDetails.get("addresses");

                            value = String.valueOf(Address.size());
                            add1 = null; add2 = null; add3 = null;
                            if(Address.size()>0)
                                add1 = Address.get(0);
                            if(Address.size()>1)
                                add2 = Address.get(1);
                            if(Address.size()>2)
                                add3 = Address.get(2);

                            if (add1!=null){
                                add_1.setVisibility(View.VISIBLE);
                                add_1.setText(add1);
                                //button1.setText("Change Address");
                            }
                            if (add2!=null){
                                add_2.setVisibility(View.VISIBLE);
                                add_2.setText(add2);
                                //button2.setText("Change Address");
                            }
                            if (add3!=null){
                                add_3.setVisibility(View.VISIBLE);
                                add_3.setText(add3);
                                //button3.setText("Change Address");
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                } else {
                    Toast.makeText(AddressOptionActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}