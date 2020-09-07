package com.example.kadakadda;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Adapters.PastOrderAdapter;
import com.example.kadakadda.Models.PastOrderItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastOrder_f#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastOrder_f extends Fragment {

    ProgressBar progressBar;
    RecyclerView pastOrderRecyclerView;
    PastOrderAdapter pastOrderAdapter;
    ArrayList<PastOrderItems> pastOrderItems = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    Context context = getContext();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PastOrder_f";
    public static final String LIST = "OrderList";
    public static final String DOWNLOADED = "Downloaded";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean downloaded;

    public PastOrder_f() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PastOrder_f.
     */
    // TODO: Rename and change types and number of parameters
    public static PastOrder_f newInstance(String param1, String param2) {
        PastOrder_f fragment = new PastOrder_f();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        downloaded = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
               // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_order_f, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        pastOrderRecyclerView = (RecyclerView) view.findViewById(R.id.pastOrderRecyclerView);
        progressBar = view.findViewById(R.id.progress_bar);

        if(savedInstanceState!=null && savedInstanceState.getBoolean(DOWNLOADED, false)){
            Log.d(TAG, "onCreateView: savedInstanceState not null");
            pastOrderItems = savedInstanceState.getParcelableArrayList(LIST);
            setUpAdapter();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            StorePastOrdersDataInArray();
        }

        return view;
    }

    public void StorePastOrdersDataInArray() {
        if (mAuth.getCurrentUser() != null) {
            fStore.collection("orders")
                    .whereEqualTo("userID", Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                    if (!documentSnapshot.getBoolean("order_cancelled") && documentSnapshot.getBoolean("order_delivered")) {

                                        pastOrderItems.add(new PastOrderItems(String.valueOf(documentSnapshot.getLong("itemNumber")), documentSnapshot.getString("total"), documentSnapshot.getString("orderID"), documentSnapshot.getDate("timePlaced").toString()));

                                    }

                                    if (documentSnapshot.getBoolean("order_cancelled") && !documentSnapshot.getBoolean("order_delivered")) {
                                        pastOrderItems.add(new PastOrderItems(String.valueOf(documentSnapshot.getLong("itemNumber")), documentSnapshot.getString("total"), documentSnapshot.getString("orderID"), "Order Cancelled"));
                                    }
                                }
                                //pastOrderAdapter.notifyDataSetChanged();
                                setUpAdapter();
                            } else {
                                Toast.makeText(getContext(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void setUpAdapter() {
        pastOrderAdapter = new PastOrderAdapter(pastOrderItems);
        pastOrderRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        pastOrderRecyclerView.setAdapter(pastOrderAdapter);
        progressBar.setVisibility(View.GONE);
        downloaded = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOADED, downloaded);
        outState.putParcelableArrayList(LIST, pastOrderItems);
    }
}