package com.example.kadakadda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kadakadda.Adapters.CartAdapter;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grpc.Context;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "Profile Activity";
    public TextInputEditText username,email,password,number,dob;
    public Spinner spinner;
    Button save;
    ImageView profileImage,changeImage;
    public Boolean allowToEdit = false;
    public TextView nameDisplay;



    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView btn_back;

    ProgressBar pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        //setting up instance
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        nameDisplay = findViewById(R.id.name);
        username=findViewById(R.id.profile_username_text);
        email=findViewById(R.id.profile_email_text);
        //password=findViewById(R.id.profile_password_text);
        number=findViewById(R.id.profile_number_text);
        dob=findViewById(R.id.dateOfBirth_text);

        pg=findViewById(R.id.progressBar3);

        btn_back=findViewById(R.id.bck_arw);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),NavigaActivity.class));
                finish();
            }
        });



        username.setHint("Username");
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    username.setHint("Name");
                else
                    username.setHint("");
            }
        });

        email.setHint("Email");
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    email.setHint("Email");
                else
                    email.setHint("");
            }
        });
        /*password.setHint("Password");
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    password.setHint("Password");
                else
                    password.setHint("");
            }
        });

         */

        number.setHint("Number");
        number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    number.setHint("Number");
                else
                    number.setHint("");
            }
        });

        dob.setHint("Date Of Birth");
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    dob.setHint("Date Of Birth");
                else
                    dob.setHint("");
            }
        });
        spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


        List<String> categories =new ArrayList<>();
        categories.add("Male");
        categories.add("Female");
        categories.add("Prefer Not to Say");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_style_bhavya,categories) {

            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setGravity(Gravity.CENTER);


                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {

                View v = super.getDropDownView(position, convertView,parent);

                ((TextView) v).setGravity(Gravity.CENTER);


                return v;

            }

        };
        spinner.setAdapter(adapter);


        //setting up save button click listener to save details........
        save = findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(allowToEdit.equals(false)) {
                    username.setEnabled(true);
                    email.setEnabled(true);
                    number.setEnabled(true);
                    dob.setEnabled(true);

                    save.setText("Save");

                    allowToEdit = true;
                }
                else
                {
                    settingUserInfoToFirebase();
                    allowToEdit = false;
                    save.setText("Edit");

                    username.setEnabled(false);
                    email.setEnabled(false);
                    number.setEnabled(false);
                    dob.setEnabled(false);
                }
            }
        });

        //setting up on image click listener to change image........
        profileImage = findViewById(R.id.profileImage);

        //setting texts to edit texts......
        try {
            pg.setVisibility(View.VISIBLE);
            fetchingUpDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*

        fStore.collection("user").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        pg.setVisibility(View.VISIBLE);

                        if(documentSnapshot.exists())
                        {
                            pg.setVisibility(View.INVISIBLE);
                            String xx=((Map<String, Object>) documentSnapshot.get("pDetails")).get("imagepath").toString();
                           // String yy=((Map<String, Object>) documentSnapshot.get("pDetails")).get("name").toString();

                            if (xx.equals(""))
                            {

                            }
                            else {

                                Picasso.get().load(Uri.parse(xx)).into(profileImage);
                            }

                           // Picasso.get().load(Uri.parse(((Map<String, Object>) documentSnapshot.get("pDetails")).get("imagepath").toString())).into(profileImage);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ProfileActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

         */

        changeImage = findViewById(R.id.changeImage);
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //opening gallery intent
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);

            }
        });

    }




    //fetching personal details from firebase..........
    public void fetchingUpDetails()
    {

        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                /*for (UserInfo profile : user.getProviderData()) {
                    Log.d(TAG, "fetchingUpDetails: Provider "+profile.getUid()+" provided by "+profile.getProviderId());

                    Log.d(TAG, "fetchingUpDetails: Name "+profile.getDisplayName());
                    if(profile.getDisplayName() != null && !profile.getDisplayName().equals("")){
                        nameDisplay.setText(profile.getDisplayName());
                        username.setText(profile.getDisplayName());
                    }
                    Uri photoUrl = profile.getPhotoUrl();

                    if(photoUrl==null || photoUrl.toString().equals("")) {
                        Log.d(TAG, "onCreate: No profile photo yet. " + photoUrl);
                    }
                    else {
                        Log.d(TAG, "fetchingUpDetails: Pic "+photoUrl.toString());
                        Picasso.get().load(photoUrl).into(profileImage);
                    }
                }*/
                try {
                    String name = user.getDisplayName();
                    Uri profileImg = user.getPhotoUrl();
                    Log.d(TAG, "fetchingUpDetails: Name " + name);
                    Log.d(TAG, "fetchingUpDetails: Profile pic " + profileImg.toString());
                    if (name != null && !name.equals("")) {
                        nameDisplay.setText(name);
                        username.setText(name);
                        Log.d(TAG, "fetchingUpDetails: Name set!");
                    }
                    //File file = new File(profileImg.toString());
                    Picasso.get().load(profileImg).into(profileImage);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }


            DocumentReference docRef = fStore.collection("user").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try {
                        if (documentSnapshot.exists()) {
                            // nameDisplay.setText(((Map<String, Object>) documentSnapshot.get("pDetails")).get("name").toString());
                            // username.setText(((Map<String, Object>) documentSnapshot.get("pDetails")).get("name").toString());
                            email.setText(((Map<String, Object>) documentSnapshot.get("pDetails")).get("mail").toString());
                            number.setText(((Map<String, Object>) documentSnapshot.get("pDetails")).get("mob").toString());
                            dob.setText(((Map<String, Object>) documentSnapshot.get("pDetails")).get("dob").toString());
                        }
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                    pg.setVisibility(View.GONE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pg.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Some error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch (NullPointerException ex)
        {
            ex.printStackTrace();
            pg.setVisibility(View.GONE);
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //profile pic section .....
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == 1000 && data != null && data.getData() != null) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    //profileImg.setImageURI(imageUri);

                    //uploading image......
                    if(imageUri != null) {
                        Log.i("image",imageUri.toString());
                        //uploadImageToFirebase(imageUri);

                        user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user != null)
                        {
                            uploadImageToFirebase(imageUri);
                        }
                    }
                }
            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {

        pg.setVisibility(View.VISIBLE);

        final StorageReference filRef = storageReference.child("users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/profile.jpg");
        filRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pg.setVisibility(View.INVISIBLE);
                                if(task.isSuccessful()) {
                                    Log.i(TAG,"Profile pic updated successfully");
                                    fetchingUpDetails();
                                }else{
                                    Log.d(TAG, "onComplete: failed to update profile");
                                }

                            }
                        });
                        
                        //Map<String, String> imagepath = new HashMap<>();
                        //imagepath.put("imagepath", uri.toString());
                        /*DocumentReference documentReference = fStore.collection("user").document(mAuth.getCurrentUser().getUid());
                        documentReference.update("pDetails.imagepath", uri.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pg.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ProfileActivity.this, "Updated Firestore Link.", Toast.LENGTH_SHORT).show();
                                    }
                                });*/

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ProfileActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String category= parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void settingUserInfoToFirebase()
    {
        final String userName = username.getText().toString();
        String emailId = email.getText().toString();
        // String pass = password.getText().toString();
        String phone = number.getText().toString();
        final String dateOfBirth = dob.getText().toString();
        String gender = spinner.getSelectedItem().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
        {
            email.setError("Enter a valid email address");
        }

        if(userName.isEmpty() || emailId.isEmpty() || phone.isEmpty() || dateOfBirth.isEmpty())
        {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
        }

        pg.setVisibility(View.VISIBLE);
        //setting username to the users firebase auth profile section...............
        user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Log.i(TAG,"Username set to user profile in firebase auth");
                        }

                    }
                });





        //updating pDetails map inside firebase.......
        DocumentReference documentReference = fStore.collection("user").document(mAuth.getCurrentUser().getUid());

        //Map<String,Object> data = new HashMap<>();
/*
        Map<String,Object> pDetails = new HashMap<>();
        pDetails.put("dob",dateOfBirth);
        pDetails.put("gender",gender);
        pDetails.put("mail",emailId);
        pDetails.put("mob",phone);
        pDetails.put("name",userName);
        //pDetails.put("imagepath","");

*/
        //data.put("pDetails",pDetails);


        documentReference.update("pDetails.dob",dateOfBirth,"pDetails.gender",gender,"pDetails.mail",emailId,"pDetails.mob",phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.i(TAG,"User Profile Updated Successfully.");
                    Toast.makeText(ProfileActivity.this, "Your profile has been updated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.i(TAG,"Some error : " + task.getException());
                    Toast.makeText(ProfileActivity.this, "Some error : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                pg.setVisibility(View.GONE);
            }
        });
    }
}