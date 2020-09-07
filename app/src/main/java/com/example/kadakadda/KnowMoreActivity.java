package com.example.kadakadda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kadakadda.Adapters.KnowMorePagerAdapter;
import com.example.kadakadda.Models.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import static com.example.kadakadda.Adapters.KnowMorePagerAdapter.REVIEW_TYPE;

public class KnowMoreActivity extends AppCompatActivity {

    KnowMorePagerAdapter pagerAdapter;
    ImageView imageViewProduct;
    TextView textViewProductName, textViewPrice, textViewReviewAggregate, textViewNumberOfReviews;
    Product product;
    ViewPager2 viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //Binding views to objects
        imageViewProduct = findViewById(R.id.image_turkey);
        textViewProductName = findViewById(R.id.turkey_txt);
        textViewPrice = findViewById(R.id.price);
        textViewNumberOfReviews = findViewById(R.id.noOfReviews);
        textViewReviewAggregate = findViewById(R.id.reviewAggregate);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab);

        //Get the product clicked by the user
        Bundle bundle = getIntent().getExtras();
        product = (Product)bundle.getParcelable(getString(R.string.knowMoreProduct));

        setProductViews();
        setUpViewPager();
    }

    private void setProductViews() {
        Picasso.get()
                .load(product.imgPath)
                .into(imageViewProduct);
        textViewProductName.setText(product.title);
        textViewPrice.setText(String.valueOf(product.price));

        //TODO: Review related textViews
    }

    public void setUpViewPager(){
        pagerAdapter = new KnowMorePagerAdapter(getSupportFragmentManager(), getLifecycle(), product, KnowMoreActivity.this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //Log.d("TAG", "onConfigureTab: "+ position);
                tab.setText(position == REVIEW_TYPE? "REVIEW" : "DETAILS");
            }
        }).attach();
    }
}