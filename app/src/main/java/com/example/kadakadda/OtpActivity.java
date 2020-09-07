package com.example.kadakadda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    public static final String PHONE_NUMBER = "phoneNumber";
    private static final String TAG = "OtpActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    FirebaseFirestore firestore;
    private String mAuthVerificationId, mPhoneNumber;

    private EditText otp_txt;
    private Button mVerifyBtn;

    private ProgressBar mOtpProgress;

    private TextView mOtpFeedback,btnresnd;;
    String nme="";
    String eml="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

       // mOtpFeedback = findViewById(R.id.otp_form_feedback);
        mOtpProgress = findViewById(R.id.progressBar2);
      otp_txt=findViewById(R.id.otp_nmbr);

        mVerifyBtn = findViewById(R.id.verify_otp);
        btnresnd=findViewById(R.id.resend);

        mPhoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
        nme=getIntent().getStringExtra("signup_name");
        eml=getIntent().getStringExtra("signup_email");
        sendVerificationCodeToUser(mPhoneNumber);
        //mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

       /* btnresnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeToUser(mPhoneNumber);
                Toast.makeText(OtpActivity.this, "sending OTP....", Toast.LENGTH_SHORT).show();
            }
        });
*/
        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String otp = otp_txt.getText().toString();

                if(otp.isEmpty() || otp.length()<6){

                    //mOtpFeedback.setVisibility(View.VISIBLE);
                    //mOtpFeedback.setText("Please fill in the form and try again.");

                    Toast.makeText(OtpActivity.this, "Fill Again", Toast.LENGTH_SHORT).show();

                } else {

                    //mOtpProgress.setVisibility(View.VISIBLE);
                    mVerifyBtn.setEnabled(false);
                    verifyCode(otp);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }

    public void sendUserToHome()
    {
        Intent homeIntent = new Intent(OtpActivity.this, NavigaActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    public void sendUserValidation() {
        mCurrentUser=mAuth.getCurrentUser();
        mCurrentUser=mAuth.getCurrentUser();

        DocumentReference docRef = firestore.collection("user").document(mCurrentUser.getUid());
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData()==null){
                            Log.d(TAG, "onComplete: Document has not been created.");
                            createNewUserDocument();
                        }else
                        {
                            sendUserToHome();
                        }
                    }
                });
    }

    private void createNewUserDocument() {
        DocumentReference docRef = firestore.collection("user").document(mCurrentUser.getUid());
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> pDetails = new HashMap<>();
        pDetails.put("addresses", new ArrayList<String>());
        pDetails.put("dob", "");
        pDetails.put("gender", "");
        pDetails.put("mail", eml);
        pDetails.put("mob", "");
        pDetails.put("name", nme);
        pDetails.put("imagepath","");

        map.put("cart", new HashMap<>());
        map.put("cart_total", 0L);
        map.put("pDetails", pDetails);
        map.put("pastOrders", new ArrayList<String>());

        docRef
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(OtpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                           sendUserToHome();
                        }
                    }
                });
    }








    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    mAuthVerificationId = s;
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                       // mOtpProgress.setVisibility(View.VISIBLE);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String codeByUser) {

        mOtpProgress.setVisibility(View.VISIBLE);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, codeByUser);
        signInTheUserByCredentials(credential);

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Signing in successfully.");
                            sendUserValidation();
                        } else {
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}