package com.example.kadakadda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kadakadda.Adapters.HomePagerAdapter;
import com.example.kadakadda.Models.CartItem;
import com.example.kadakadda.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.kadakadda.Adapters.HomePagerAdapter.MEAT_TYPE;
import static com.example.kadakadda.CartActivity.PRODUCT_AVAILABILITY;
import static com.example.kadakadda.CartActivity.PRODUCT_DESCRIPTION;
import static com.example.kadakadda.CartActivity.PRODUCT_IMGPATH;
import static com.example.kadakadda.CartActivity.PRODUCT_PRICE;
import static com.example.kadakadda.CartActivity.PRODUCT_TITLE;
import static com.example.kadakadda.CartActivity.PRODUCT_TYPE;

public class NavigaActivity extends AppCompatActivity {

    public static final String TAG = "NavigaActivity";
    public static final String PROD = "products";
    public static final String LOADED = "Loaded";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    Gson gson;
    ArrayList<Product> products;
    ArrayList<CartItem> cartItems;
    HomePagerAdapter pagerAdapter;

    boolean downloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_Empcart);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        gson = new Gson();
        cartItems = new ArrayList<>();
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setToolbarListeners();
        setNavigationDrawerListeners();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        products = new ArrayList<>();
        if(savedInstanceState!=null){
            downloaded = savedInstanceState.getBoolean(LOADED);
            if(downloaded)
                products = savedInstanceState.getParcelableArrayList(PROD);
        }
        fetchProductsList();
        headerprofile();

    }

    public void headerprofile() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview=navigationView.getHeaderView(0);
        final ImageView prfl=headerview.findViewById(R.id.image_header);
        final TextView nm=headerview.findViewById(R.id.nmtxt);

        String xx = null;
        String yy="";
        try{
            xx=mAuth.getCurrentUser().getPhotoUrl().toString();
            Log.d(TAG, "headerprofile: Profile Pic "+xx.toString());
            yy=mAuth.getCurrentUser().getDisplayName().toString();
            Log.d(TAG, "headerprofile: Display Name "+yy.toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if (xx!=null &&xx.toString().equals("")) {
            Log.d(TAG, "headerprofile: No profile photo set");
        }
        else {
            Picasso.get().load(xx).into(prfl);
        }

        if (yy.equals("")) {
            Log.d(TAG, "headerprofile: No profile name set");
            nm.setText("New User");
        }else {
            nm.setText(yy);
        }

        prfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });


    }



    public void cartEntry() {
        firebaseFirestore.collection("user").document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Map<String, Object> map = task.getResult().getData();
                            Map<String, Object> cart = (Map<String, Object>) map.get("cart");
                            if(cart == null || cart.isEmpty()){
                                //TODO : Navigate to empty cart activity
                                Intent intent = new Intent(NavigaActivity.this, EmptyCartActivity.class);
                                startActivity(intent);
                            }
                            else{
                                //open Cart
                                Intent intent = new Intent(NavigaActivity.this, CartActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(NavigaActivity.this, "There was some error." + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }



    private void setNavigationDrawerListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.nav_Home:
                        startActivity(new Intent(getApplicationContext(),NavigaActivity.class));
                        finish();
                        break;
                    case R.id.nav_orders:
                        intent = new Intent(NavigaActivity.this, MyOrders.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_favorders:
                        intent = new Intent(NavigaActivity.this, FavoriteOrdersActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_cart:
                        cartEntry();
                        break;
                    case R.id.nav_notification:
                        intent = new Intent(NavigaActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_address:
                        //intent = new Intent(NavigaActivity.this, UserAddress.class);
                        //startActivity(intent);
                        break;
                    case R.id.nav_payment:
                        //intent = new Intent(NavigaActivity.this, PaymentActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent = new Intent(NavigaActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_contact:
                        intent = new Intent(NavigaActivity.this, ContactActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_signout:
                        if(user!=null){
                            mAuth.signOut();
                            intent = new Intent(NavigaActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.feedback:
                        intent = new Intent(NavigaActivity.this, FeedbackActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected Value: " + item.getItemId());
                }
                return false;
            }
        });
    }

    private void setToolbarListeners() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.notification:
                        intent = new Intent(NavigaActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.cart:
                        cartEntry();
                        break;
                    case R.id.orders:
                        intent = new Intent(NavigaActivity.this, MyOrders.class);
                        startActivity(intent);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: "+item.getItemId());
                }
                return false;
            }
        });
    }

    /*
        fetchProductsList() loads product list from cloud and populates products ArrayList.
        It calls storeProductListLocal() to store the product list in local storage.
     */
    public void fetchProductsList(){
        if(!downloaded) {
            firebaseFirestore.collection("products")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //TODO: Set ProgressBarVisibility
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot qds : task.getResult()) {
                                    Map<String, Object> data = qds.getData();
                                    Product product = new Product(
                                            qds.getId(),
                                            (Boolean) data.get(PRODUCT_AVAILABILITY),
                                            (String) data.get(PRODUCT_DESCRIPTION),
                                            (String) data.get(PRODUCT_IMGPATH),
                                            (Long) data.get(PRODUCT_PRICE),
                                            (String) data.get(PRODUCT_TITLE),
                                            (String) data.get(PRODUCT_TYPE)
                                    );
                                    products.add(product);
                                    Log.i("HI", "Added " + product.id);
                                }
                                //pagerAdapter.notifyDataSetChanged();
                                storeProductListLocal();
                            } else {
                                Log.i(TAG, task.getException().getLocalizedMessage());
                                Toast.makeText(NavigaActivity.this, "Sorry, couldn't load data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else{
            fetchCloudCart();
        }
    }

    /*
        storeProductListLocal() stores product list locally and calls for loadProductListLocal to
        reload list from local storage and advance to loading user cart.
     */
    private void storeProductListLocal() {
        String productList = gson.toJson(products);
        editor.putString(getString(R.string.product_list), productList);
        editor.commit();
        Log.d(TAG, "storeProductListLocal: Product List written to SharedPreferences!");
        downloaded = true;
        fetchCloudCart();
    }

    /*
        fetchCloudCart() fetches the cart present in the user profile document.
     */
    public void fetchCloudCart() {
        firebaseFirestore.collection("user")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            try {
                                Map<String, Object> docData = task.getResult().getData();

                                HashMap<String, Object> cart = (HashMap<String, Object>) docData.get("cart");

                                if (cart == null || cart.isEmpty()) {
                                    Log.d(TAG, "onComplete: Empty Cloud Cart!!");
                                    throw new IllegalStateException("Cart is null or does not exist.");
                                }
                                for (HashMap.Entry<String, Object> entry : cart.entrySet()) {
                                    Product p = findProduct(entry.getKey());
                                    if (p == null) {
                                        throw new IllegalStateException("Bad product key!");
                                    }
                                    cartItems.add(new CartItem(p, (Double) entry.getValue()));
                                }
                                String userCartLocal = gson.toJson(cartItems);
                                editor.putString(getString(R.string.user_cart), userCartLocal);
                                editor.commit();
                                Log.i(TAG, "onComplete: Updating local cart");
                                progressBar.setVisibility(View.GONE);

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.d(TAG, "onComplete: There was a problem loading the cloud cart.");
                                editor.putString(getString(R.string.user_cart), null);
                                editor.commit();
                                Log.i(TAG, "onComplete: "+e.getLocalizedMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Toast.makeText(NavigaActivity.this, "Failed to get cloud cart.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        //Set pagerAdapters, viewPager and TabLayoutMediator.
        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getLifecycle(), products, NavigaActivity.this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(position==MEAT_TYPE?"MEAT":"EGGS");
            }
        }).attach();

    }

    /*
        findProduct() is used to find product by id.
        Called by fetchCloudCart to use a Product object for creating CartItem objects.
        null return means that the cloud cart is compromised.
     */
    public Product findProduct(String id){
        for(Product product:products){
            if(product.id.equals(id))
                return product;
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(LOADED, downloaded);
        outState.putParcelableArrayList(PROD, products);
    }
}