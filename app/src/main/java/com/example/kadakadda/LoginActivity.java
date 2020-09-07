package com.example.kadakadda;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.Login;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;

    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;
   private SpecialFbLoginButton fbLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private AuthCredential credential;


    private CardView btnsign,fbbtn,btnsgnup;
    private GoogleSignInClient googleSignInClient;
    private String TAG = "LoginActivity";

    private EditText mPhoneNumber,logmail,logpswrd;

    private Button mGenerateBtn,forgetbtn,loginbtn;
    private ProgressBar progressBarr;

    private TextView mLoginFeedbackText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String FB_TAG = "FacebookAuthentication";
    private static final Integer RC_SIGN_IN = 1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toast.makeText(this, "Hi!!", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        logmail=findViewById(R.id.user_id);
        //logpswrd=findViewById(R.id.password);
      // entr=findViewById(R.id.fbcardview);
        progressBarr=findViewById(R.id.progressBar);

        mPhoneNumber = findViewById(R.id.input_number);
        mGenerateBtn = findViewById(R.id.send_otp);
        btnsign=findViewById(R.id.googlecardviews);
        fbbtn=findViewById(R.id.fbcardview);

        //mLoginProgress = findViewById(R.id.login_progress_bar);
       // mLoginFeedbackText = findViewById(R.id.login_form_feedback);

        //Special Facebook Login button : fbLoginButton


        fbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),fbEntryActivity.class));
            }
        });


/*
        fbLoginButton = (SpecialFbLoginButton) findViewById(R.id.specialFbLoginButton2);
        fbLoginButton.setReadPermissions("email", "public_profile");
*/
        //signInButton = findViewById(R.id.googlecardview);
        btnsgnup=findViewById(R.id.mailcardview);


        btnsgnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });


        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarr.setVisibility(View.VISIBLE);
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
                    Toast.makeText(LoginActivity.this, "Enter correct mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarr.setVisibility(View.VISIBLE);
                    mGenerateBtn.setEnabled(false);

                    /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks
                    );*/
                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                    intent.putExtra(PHONE_NUMBER, phone_number);
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
                mLoginFeedbackText.setText("Verification Failed, please try again.");
                mLoginFeedbackText.setVisibility(View.VISIBLE);
                progressBarr.setVisibility(View.INVISIBLE);
                mGenerateBtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);
            }
        };
        mCallbackManager = CallbackManager.Factory.create();

        //START: Facebook Authentication Code
/*
     FacebookSdk.sdkInitialize(getApplicationContext());

        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(FB_TAG, "onSuccess: " + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(FB_TAG, "onCancel()");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(FB_TAG, "onError:" + error);
            }
        });
*/
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mCurrentUser = mAuth.getCurrentUser();
                if(mCurrentUser!=null){
                }
                else{
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };
        //END : Facebook Authentication Code

        //START: Gmail Authentication Code
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        //END: Gmail Authentication Code



    }





    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        if(mCurrentUser != null){
            sendUserToHome();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserToHome();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mLoginFeedbackText.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                //mLoginFeedbackText.setText("There was an error verifying OTP");
                            }
                        }
                        progressBarr.setVisibility(View.INVISIBLE);
                        mGenerateBtn.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(LoginActivity.this, NavigaActivity.class);
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

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(FB_TAG, "handleFacebookToken: " + accessToken);

        credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(FB_TAG, "Successful Sign IN");
                    Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    //mCurrentUser = mAuth.getCurrentUser();
                    //sendUserToHome();
                    sendUserValidation();

                }else{
                    Log.d(FB_TAG, "Sign in Failed." + task.getException().getLocalizedMessage());
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    fbLoginButton.getTheLoginManager().logOut();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            mAuth.removeAuthStateListener(authStateListener);
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
                        Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Success");
                        //sendUserToHome();
                        sendUserValidation();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(LoginActivity.this, "document create", Toast.LENGTH_SHORT).show();
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
        pDetails.put("mail", "");
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
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            sendUserToHome();
                        }
                    }
                });
    }
}