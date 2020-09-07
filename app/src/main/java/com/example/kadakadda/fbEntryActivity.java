package com.example.kadakadda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fbEntryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore firestore;

    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;
    private SpecialFbLoginButton fbLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private AuthCredential credential;



    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String FB_TAG = "FacebookAuthentication";
    private static final Integer RC_SIGN_IN = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_entry);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();



        fbLoginButton = (SpecialFbLoginButton) findViewById(R.id.specialFbLoginButton);
        fbLoginButton.setReadPermissions("email", "public_profile");


        //START: Facebook Authentication Code
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
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



    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }



    private void sendUserToHome() {
        Intent homeIntent = new Intent(fbEntryActivity.this, NavigaActivity.class);
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
                    Toast.makeText(fbEntryActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    //mCurrentUser = mAuth.getCurrentUser();
                    //sendUserToHome();
                    sendUserValidation();

                }else{
                    Log.d(FB_TAG, "Sign in Failed." + task.getException().getLocalizedMessage());
                    Toast.makeText(fbEntryActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(fbEntryActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                    //sendUserToHome();
                   sendUserValidation();
                } else {
                    Toast.makeText(fbEntryActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    public void sendUserValidation() {
        mCurrentUser=mAuth.getCurrentUser();

        DocumentReference docRef = firestore.collection("user").document(mCurrentUser.getUid());
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData()==null){
                            Toast.makeText(fbEntryActivity.this, "document create", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(fbEntryActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            sendUserToHome();
                        }
                    }
                });
    }



}