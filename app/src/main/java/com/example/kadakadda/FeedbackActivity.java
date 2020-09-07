package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mfirestore;
    String uid;

    Button sendFeedback;
    EditText feedbackEditText;
    EditText emailEditText;

    ProgressBar progressBar;
    Map<String ,String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mfirestore = FirebaseFirestore.getInstance();
        uid = currentUser.getUid();

        sendFeedback = findViewById(R.id.button2);
        feedbackEditText = findViewById(R.id.editTextTextMultiLine);
        emailEditText = findViewById(R.id.editTextTextPersonName);
        progressBar=findViewById(R.id.progressBar4);

        map = new HashMap<>();

        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String feedback = feedbackEditText.getText().toString();
                String email = emailEditText.getText().toString();
                if (feedback.equals("")||email.equals("")){
                    Toast.makeText(FeedbackActivity.this, "Please fill the feedback form first", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    sendFeedback.setEnabled(false);
                    map.put("Feedback",feedback);
                    map.put("Email",email);
                    mfirestore.collection("Feedback").document(uid).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            sendFeedback.setEnabled(true);
                            Toast.makeText(FeedbackActivity.this, "We have got your feedback. We will try to resolve your problem and contact you on email if required ", Toast.LENGTH_SHORT).show();

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(FeedbackActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    sendFeedback.setEnabled(true);
                                }
                            });


                }
            }

        });
    }
}