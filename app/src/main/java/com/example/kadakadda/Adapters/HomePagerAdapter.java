package com.example.kadakadda.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.kadakadda.ItemsFragment;
import com.example.kadakadda.Models.Product;

import java.util.ArrayList;

/*
    Handles the NavigaActivity Tablayout and ViewPager. It accepts entire Product ArrayList
    and segregates them into 2 lists, MEAT and EGG. It populates its Fragments (ItemsFragment)
    with one Fragment corresponding to one type of Product - either EGG or MEAT.
 */
public class HomePagerAdapter extends FragmentStateAdapter {

    public static final int MEAT_TYPE = 0;
    public static final int EGG_TYPE = 1;

    private ArrayList<Product> products, eggProducts=new ArrayList<>(), meatProducts=new ArrayList<>();
    private Context context;

    public HomePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Product> products, Context context) {
        super(fragmentManager, lifecycle);
        this.products = products;
        this.context = context;

        meatProducts = new ArrayList<>();
        eggProducts = new ArrayList<>();

        //Segregation
        for(Product product : this.products){
            if(product.type.equals("eggs"))
                eggProducts.add(product);
            else
                meatProducts.add(product);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == MEAT_TYPE)
            //Creates MEAT fragment having only meat products in its RecyclerView
            return new ItemsFragment(MEAT_TYPE, context, meatProducts);
        else
            //Creates EGG fragment having only egg products in its RecyclerView
            return new ItemsFragment(EGG_TYPE, context, eggProducts);
    }
}
