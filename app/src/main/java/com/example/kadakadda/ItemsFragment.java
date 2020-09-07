package com.example.kadakadda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Adapters.HomeItemAdapter;
import com.example.kadakadda.Models.CartItem;
import com.example.kadakadda.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kadakadda.Adapters.HomePagerAdapter.EGG_TYPE;
import static com.example.kadakadda.Adapters.HomePagerAdapter.MEAT_TYPE;

public class ItemsFragment extends Fragment implements HomeItemAdapter.HomeItemListener {

    private static final String TAG = "ItemsFragment";
    private ArrayList<Product> products;
    private RecyclerView recyclerView;
    private HomeItemAdapter adapter;
    private Context context;
    int foodType;

    ArrayList<CartItem> cartItems;

    Gson gson;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public ItemsFragment(){}

    public ItemsFragment(int foodType, Context context, ArrayList<Product> products){
        this.foodType = foodType;
        this.context = context;
        this.products = products;
        cartItems = new ArrayList<>();
        gson = new Gson();
        sharedPreferences = this.context.getSharedPreferences(this.context.getString(R.string.shared_preferences), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new HomeItemAdapter(foodType, context, products, ItemsFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        recyclerView = view.findViewById(R.id.egg_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onKnowMore(int itemPosition) {
        //TODO: Navigate to KnowMoreActivity with all data.
        Intent intent = new Intent(context, KnowMoreActivity.class);
        intent.putExtra(context.getString(R.string.knowMoreProduct), products.get(itemPosition));
        startActivity(intent);
    }

    @Override
    public void onAddCart(int itemPosition) {
        String productID = products.get(itemPosition).id;

        //Load Local Cart
        String storedCart = sharedPreferences.getString(context.getString(R.string.user_cart), (new JSONObject()).toString());
        try {
            java.lang.reflect.Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            cartItems = gson.fromJson(storedCart, type);
        }catch (Exception e){
            //There is no local cart available in SharedPreferences
            e.printStackTrace();
            cartItems = new ArrayList<>();
        }


        //Search if item is already present.
        boolean exists = false;
        if(cartItems.size()!=0) {
            for (CartItem item : cartItems) {
                if (item.product.id.equals(productID)) {
                    exists = true;
                    break;
                }
            }
        }

        if(exists)
            Toast.makeText(context, "Item already added to cart.", Toast.LENGTH_SHORT).show();
        else{
            //Add to local cart
            CartItem cartItem = new CartItem(products.get(itemPosition), 1L);
            cartItems.add(cartItem);

            //Store cart on SharedPreferences
            String userCartLocal = gson.toJson(cartItems);
            editor.putString(context.getString(R.string.user_cart), userCartLocal);
            editor.commit();

            //Update Cloud Cart
            updateCloudCart();
        }
    }

    /*
        updateCloudCart() syncs the local changes with the server.
     */
    public void updateCloudCart(){
        HashMap<String, Double> cart = createCart();
        DocumentReference documentReference = firebaseFirestore.collection("user")
                .document(user.getUid());
        documentReference.update("cart", cart)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Updated Cloud Cart");
                            //adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(context, "Failed to update cloud cart." + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    /*
        createCart() converts the local cart to a Hashmap<String, Long> to enable the details
        to be uploaded to Firestore.
     */
    public HashMap<String, Double> createCart(){
        HashMap<String, Double> cart = new HashMap<>();
        for(CartItem cartItem:cartItems){
            cart.put(cartItem.product.id, cartItem.quantity);
        }
        return cart;
    }
}
