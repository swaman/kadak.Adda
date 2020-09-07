package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class Track extends AppCompatActivity {

    public String itemNum,total,orderId,timePlaced,deliveryAddress;

    public TextView itemNo,totalNo,orderIdNo,placedTime,address,txt1,txt2,txt3,txt4, textViewBack;

    public boolean orderDelivered = false;
    public boolean orderOutForDelivery = false;
    public boolean orderProcessed = false;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    ImageView circle1,circle2,circle3,circle4,circle5,circle6,circle7,circle8,circle9,circle10,circle11,circle12,circleBig1,circleBig2,circleBig3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        itemNum = intent.getStringExtra("itemNumber");
        total = intent.getStringExtra("total");
        orderId = intent.getStringExtra("orderId");
        timePlaced = intent.getStringExtra("timePlaced");

        itemNo = findViewById(R.id.itemNo);
        totalNo = findViewById(R.id.totalNo);
        orderIdNo = findViewById(R.id.orderId);
        placedTime = findViewById(R.id.placedDate);
        address = findViewById(R.id.address);

        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);
        circle5 = findViewById(R.id.circle5);
        circle6 = findViewById(R.id.circle6);
        circle7 = findViewById(R.id.circle7);
        circle8 = findViewById(R.id.circle8);
        circle9 = findViewById(R.id.circle9);
        circle10 = findViewById(R.id.circle10);
        circle11 = findViewById(R.id.circle11);
        circle12 = findViewById(R.id.circle12);

        circleBig1 = findViewById(R.id.circleBig1);
        circleBig2 = findViewById(R.id.circleBig2);
        circleBig3 = findViewById(R.id.circleBig3);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        textViewBack = findViewById(R.id.textView2);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyOrders.class));
                finish();
            }
        });


        //setting up texts to various text views of layout.........

        itemNo.setText(itemNum);
        totalNo.setText(total);
        orderIdNo.setText(orderId);
        placedTime.setText(timePlaced);


        //fetching address and various booleans to specify the track of the product........
        fetchTrackStatus();

        address.setText(deliveryAddress);





    }

    private void fetchTrackStatus() {

        fStore.collection("orders")
                .whereEqualTo("orderID",orderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot qds : Objects.requireNonNull(task.getResult()))
                            {
                                Map<String,Object> docData = qds.getData();

                                deliveryAddress = qds.getString("deliveryaddress");

                                orderDelivered = qds.getBoolean("order_delivered");
                                orderOutForDelivery = qds.getBoolean("order_outForDelivery");
                                orderProcessed = qds.getBoolean("order_processed");


                                //setting up track of order.............
                                settingUpTrack();

                            }
                        }
                        else
                        {
                            Toast.makeText(Track.this, "Some Error Occurred : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void settingUpTrack()
    {
        if(orderOutForDelivery && (orderProcessed) && (!orderDelivered))
        {
            circle1.setImageResource(R.drawable.circlefill);
            circle2.setImageResource(R.drawable.circlefill);
            circle3.setImageResource(R.drawable.circlefill);
            circle4.setImageResource(R.drawable.circlefill);

            circleBig1.setImageResource(R.drawable.check);


            circle5.setImageResource(R.drawable.circlefill);
            circle6.setImageResource(R.drawable.circlefill);
            circle7.setImageResource(R.drawable.circlefill);
            circle8.setImageResource(R.drawable.circlefill);

            circleBig2.setImageResource(R.drawable.check);

            txt2.setText("Order" + orderId + "from KadakAdda is ready to pick up.");
            txt2.setVisibility(View.VISIBLE);

            txt3.setVisibility(View.VISIBLE);


        }

        if(orderDelivered && orderOutForDelivery && orderProcessed)
        {
            circle1.setImageResource(R.drawable.circlefill);
            circle2.setImageResource(R.drawable.circlefill);
            circle3.setImageResource(R.drawable.circlefill);
            circle4.setImageResource(R.drawable.circlefill);

            circleBig1.setImageResource(R.drawable.check);


            circle5.setImageResource(R.drawable.circlefill);
            circle6.setImageResource(R.drawable.circlefill);
            circle7.setImageResource(R.drawable.circlefill);
            circle8.setImageResource(R.drawable.circlefill);

            circleBig2.setImageResource(R.drawable.check);


            circle9.setImageResource(R.drawable.circlefill);
            circle10.setImageResource(R.drawable.circlefill);
            circle11.setImageResource(R.drawable.circlefill);
            circle12.setImageResource(R.drawable.circlefill);

            circleBig3.setImageResource(R.drawable.check);

            txt2.setText("Order" + orderId + "from KadakAdda is ready to pick up.");
            txt2.setVisibility(View.VISIBLE);

            txt3.setVisibility(View.VISIBLE);

            txt4.setVisibility(View.VISIBLE);
        }
        if((!orderOutForDelivery) && orderProcessed && (!orderDelivered))
        {
            circle1.setImageResource(R.drawable.circlefill);
            circle2.setImageResource(R.drawable.circlefill);
            circle3.setImageResource(R.drawable.circlefill);
            circle4.setImageResource(R.drawable.circlefill);

            circleBig1.setImageResource(R.drawable.check);

            txt2.setText("Order" + orderId + "from KadakAdda is ready to pick up.");
            txt2.setVisibility(View.VISIBLE);
        }
    }
}