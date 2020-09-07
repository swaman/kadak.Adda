package com.example.kadakadda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Adapters.OnGoingParentAdapter;
import com.example.kadakadda.Models.OnGoingOrderParentItems;
import com.example.kadakadda.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ongoing_f extends Fragment implements OnGoingParentAdapter.OnGoingParentListener {

    public static final String PRODUCT_AVAILABILITY = "availability";
    public static final String PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCT_IMGPATH = "imgPath";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_TITLE = "title";
    public static final String PRODUCT_TYPE = "type";
    public static final String TAG = "OnGoingParentActivity";

    public static final String LIST = "OrderList";
    public static final String DOWNLOADED = "Downloaded";

    public RecyclerView onGoingParentRecyclerView,childRecyclerView;
    ProgressBar progressBar;
    public OnGoingParentAdapter onGoingParentAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    Gson gson;

    Context mContext = this.getContext();
    boolean downloaded;

    ArrayList<Product> products;
    ArrayList<OnGoingOrderParentItems> onGoingOrderParentItemsArrayList = new ArrayList<>();

    public Ongoing_f() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ongoing_f, container, false);

        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        products = new ArrayList<>();

        mContext = view.getContext();
        onGoingParentRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewParent);
        progressBar = view.findViewById(R.id.progress_bar);

        if(savedInstanceState!=null) {
            Log.d(TAG, "onCreateView: savedInstanceState not null");
            downloaded = savedInstanceState.getBoolean(DOWNLOADED, false);
            if (downloaded)
                onGoingOrderParentItemsArrayList = savedInstanceState.getParcelableArrayList(LIST);
        }
        else
            downloaded = false;

        loadProductListLocal();

        //childRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewChild);

        //setting parent recycler view.....


        //StoreOngoingOrderListInArray();

        /*track = (TextView) view.findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openingTrackActivity();
            }
        });*/

        return view;
    }

    private void openingTrackActivity(String itemNum,String totalPrice,String orderId,String timePlaced) {
        Intent intent = new Intent(mContext, Track.class);

        intent.putExtra("itemNumber",itemNum);
        intent.putExtra("total",totalPrice);
        intent.putExtra("orderId",orderId);
        intent.putExtra("timePlaced",timePlaced);

        startActivity(intent);
    }

    public void StoreOngoingOrderListInArray() {
        if (user != null) {
            firebaseFirestore.collection("orders")
                    .whereEqualTo("userID", user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                    Map<String, Object> docData = documentSnapshot.getData();

                                    if (docData.isEmpty()) {
                                        throw new NullPointerException("User profile not exist");
                                    } else {
                                        if (!documentSnapshot.getBoolean("order_cancelled") && !documentSnapshot.getBoolean("order_delivered")) {
                                            HashMap<String, Object> itemsList = (HashMap<String, Object>) docData.get("items");
                                            onGoingOrderParentItemsArrayList.add(
                                                    new OnGoingOrderParentItems(
                                                            documentSnapshot.getString("itemNumber"),
                                                            documentSnapshot.getString("total"),
                                                            documentSnapshot.getString("orderID"),
                                                            documentSnapshot.getDate("timePlaced").toString(),
                                                            itemsList, documentSnapshot.getString("delivered By")
                                                    )
                                            );
                                        }
                                    }
                                }
                                downloaded = true;
                                setUpAdapter();
                            } else {
                                Toast.makeText(getContext(), "Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

        }
    }

    private void setUpAdapter() {
        onGoingParentAdapter = new OnGoingParentAdapter(onGoingOrderParentItemsArrayList, products, mContext, Ongoing_f.this);
        onGoingParentRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        onGoingParentRecyclerView.setAdapter(onGoingParentAdapter);
        onGoingParentAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    public void loadProductListLocal() {
        try{
            String storedProductList = sharedPreferences.getString(getString(R.string.product_list), (new JSONObject()).toString());
            java.lang.reflect.Type type = new TypeToken<ArrayList<Product>>(){}.getType();
            products =  gson.fromJson(storedProductList, type);
            Log.i(TAG, "loadProductListLocal: "+storedProductList);
            if(downloaded){

                setUpAdapter();
            }else {
                progressBar.setVisibility(View.VISIBLE);
                StoreOngoingOrderListInArray();
            }
        }catch(Exception e){
            e.printStackTrace();
            fetchProductsList();
        }
    }

    /*
       fetchProductsList() loads product list from cloud and populates products ArrayList.
       It calls storeProductListLocal() to store the product list in local storage.
    */
    public void fetchProductsList(){
        firebaseFirestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //TODO: Set ProgressBarVisibility
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot qds: task.getResult()){
                                Map<String, Object> data = qds.getData();
                                Product product = new Product(
                                        qds.getId(),
                                        (Boolean)data.get(PRODUCT_AVAILABILITY),
                                        (String)data.get(PRODUCT_DESCRIPTION),
                                        (String)data.get(PRODUCT_IMGPATH),
                                        (Long)data.get(PRODUCT_PRICE),
                                        (String)data.get(PRODUCT_TITLE),
                                        (String)data.get(PRODUCT_TYPE)
                                );
                                products.add(product);
                            }

                            storeProductListLocal();
                        }
                        else{
                            Log.i(TAG, task.getException().getLocalizedMessage());
                        }
                    }
                });
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
        loadProductListLocal();
    }


    @Override
    public void onChildTrackClicked(int position) {
        OnGoingOrderParentItems currentOnGoingOrderItem = onGoingOrderParentItemsArrayList.get(position);
        openingTrackActivity(currentOnGoingOrderItem.getItemNum(), currentOnGoingOrderItem.getTotalCost(), currentOnGoingOrderItem.getOrderId(), currentOnGoingOrderItem.getDateOrdered());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOADED, downloaded);
        outState.putParcelableArrayList(LIST, onGoingOrderParentItemsArrayList);
    }
}