package com.example.kadakadda.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kadakadda.DetailsFragment;
import com.example.kadakadda.Models.Product;
import com.example.kadakadda.ReviewFragment;

public class KnowMorePagerAdapter extends FragmentStateAdapter {

    public static final int DETAILS_TYPE = 0;
    public static final int REVIEW_TYPE = 1;

    Product product;
    Context context;

    public KnowMorePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Product product, Context context) {
        super(fragmentManager, lifecycle);
        this.product = product;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == REVIEW_TYPE)
            return new ReviewFragment(product, context);
        else
            return new DetailsFragment(product, context);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
