package com.example.kadakadda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.kadakadda.OtpActivity.PHONE_NUMBER;

public class SignupActivity extends AppCompatActivity {


    EditText nm,mail;
    EditText logpswrd;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;


    private CallbackManager mCallbackManager;


    private AuthCredential credential;


    private CardView btnsign;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "LoginActivity";

    private EditText mPhoneNumber;

    private Button mGenerateBtn;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final Integer RC_SIGN_IN = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        mail=(EditText)findViewById(R.id.user_id);
        nm=(EditText)findViewById(R.id.user_name);
        //logpswrd=findViewById(R.id.password);
        // entr=findViewById(R.id.fbcardview);

        mPhoneNumber = findViewById(R.id.Mob_nmbr);
        mGenerateBtn = findViewById(R.id.lets_go);
        btnsign=findViewById(R.id.googlecardview);






        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });





        mGenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone_number ="";
                phone_number=mPhoneNumber.getText().toString();

                String complete_phone_number = "+91"  + phone_number;

                if(phone_number.equals("")||phone_number.length()<10){
                    //mLoginFeedbackText.setText("Please fill in the form to continue.");
                    //mLoginFeedbackText.setVisibility(View.VISIBLE);
                    Toast.makeText(SignupActivity.this, "Enter correct mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    //mLoginProgress.setVisibility(View.VISIBLE);
                    mGenerateBtn.setEnabled(false);

                    /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks
                    );*/
                    Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
                    intent.putExtra(PHONE_NUMBER, phone_number);
                    intent.putExtra("signup_name",nm.getText().toString());
                    intent.putExtra("signup_email",mail.getText().toString());
                    startActivity(intent);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mGenerateBtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(SignupActivity.this, OtpActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);
            }
        };


        //START: Gmail Authentication Code
        mCallbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        //END: Gmail Authentication Code





    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String email = mail.getText().toString();
                            String password = logpswrd.getText().toString();

                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            mAuth.getCurrentUser().linkWithCredential(credential)
                                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                sendUserToHome();
                                            } else {

                                                Toast.makeText(SignupActivity.this, task.getException().toString().substring(task.getException().toString().indexOf(" ")) , Toast.LENGTH_SHORT).show();
                                                sendUserToHome();

                                            }

                                        }
                                    });

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // mLoginFeedbackText.setVisibility(View.VISIBLE);
                                Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                //mLoginFeedbackText.setText("There was an error verifying OTP");
                            }
                        }
                        // mLoginProgress.setVisibility(View.INVISIBLE);
                        mGenerateBtn.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(SignupActivity.this, NavigaActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    //START: Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    //END: Facebook


    //START: Google
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(this, "Signed In Successfully.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch (ApiException e){
            Toast.makeText(this, "Sign in Failed. " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {

        credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //TODO : Success
                    Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Success");
                    //sendUserToHome();
                    sendUserValidation();
                } else {
                    Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    //END: Google


    public void sendUserValidation() {
        mCurrentUser=mAuth.getCurrentUser();

        DocumentReference docRef = firestore.collection("user").document(mCurrentUser.getUid());
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData()==null){
                            Toast.makeText(SignupActivity.this, "document create", Toast.LENGTH_SHORT).show();
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
        pDetails.put("mail","");
        pDetails.put("mob", "");
        pDetails.put("name", "");
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
                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            sendUserToHome();
                        }
                    }
                });
    }

}