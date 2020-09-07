package com.example.kadakadda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Models.Product;
import com.example.kadakadda.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.HomeItemViewHolder> {

    int foodType;
    Context context;
    ArrayList<Product> products = new ArrayList<>();
    public HomeItemListener listener;

    public HomeItemAdapter(int foodType, Context context, ArrayList<Product> products, HomeItemListener listener) {
        this.foodType = foodType;
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_row_item2, parent, false);
        return new HomeItemViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeItemViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textViewName.setText(product.title);
        holder.textViewPrice.setText("₹ " + product.price);
        holder.textViewDescription.setText(product.description);
        Picasso.get()
                .load(product.imgPath)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        //TODO
        //if(products==null)
         //   return 0;
        //else
            return products.size();
    }

    public static class HomeItemViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDescription, textViewPrice, textViewName;
        ImageView imageView;
        Button buttonKnowMore, buttonAddToCart;
        HomeItemListener itemListener;

        public HomeItemViewHolder(@NonNull View itemView, HomeItemListener listener) {
            super(itemView);
            itemListener = listener;
            textViewName = itemView.findViewById(R.id.name);
            textViewPrice = itemView.findViewById(R.id.price);
            textViewDescription = itemView.findViewById(R.id.description_food);
            imageView = itemView.findViewById(R.id.food_image);
            buttonKnowMore = itemView.findViewById(R.id.knowMore);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);

            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onAddCart(getAdapterPosition());
                }
            });

            buttonKnowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onKnowMore(getAdapterPosition());
                }
            });
        }
    }

    /*
        HomeItemListener enables the Item ViewHolders to interact with ItemsFragment.
        itemPosition supplies the AdapterPosition to the function implementation so that the
        ItemsFragment can get hold of the exact Product object from the products ArrayList.
     */
    public interface HomeItemListener{

        //Opens KnowMoreActivity with all info of a product (position via itemPosition). Content
        //is supplied via Intent.
        void onKnowMore(int itemPosition);

        //Checks for presence of an item in cart. If not already present, adds the particular
        //product to the cart with default quantity 1.
        void onAddCart(int itemPosition);
    }
}
