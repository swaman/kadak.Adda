package com.example.kadakadda;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kadakadda.Models.Product;

public class ReviewFragment extends Fragment {

    Product product;
    Context context;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public ReviewFragment(Product product, Context context){
        this.product = product;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }
}