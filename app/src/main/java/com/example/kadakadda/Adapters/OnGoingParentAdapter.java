package com.example.kadakadda.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Models.OnGoingOrderChildItem;
import com.example.kadakadda.Models.OnGoingOrderParentItems;
import com.example.kadakadda.Models.Product;
import com.example.kadakadda.R;

import java.util.ArrayList;
import java.util.HashMap;

public class OnGoingParentAdapter extends RecyclerView.Adapter<OnGoingParentAdapter.OnGoingParentViewHolder> implements OngoingChildAdapter.OnGoingChildListener {


    ArrayList<OnGoingOrderParentItems> onGoingOrderParentItems;
    ArrayList<Product> products;
    Context context;

    ArrayList<OnGoingOrderChildItem> onGoingOrderChildItems = new ArrayList<>();

    OngoingChildAdapter ongoingChildAdapter;
    OnGoingParentListener onGoingParentListener;

    public OnGoingParentAdapter(ArrayList<OnGoingOrderParentItems> onGoingOrderParentItems1, ArrayList<Product> products1, Context context1, OnGoingParentListener listener) {
        this.onGoingOrderParentItems = onGoingOrderParentItems1;
        this.products = products1;
        this.context = context1;
        onGoingParentListener = listener;
    }

    @NonNull
    @Override
    public OnGoingParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.parent_row,parent,false);
        return new OnGoingParentViewHolder(view, onGoingParentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OnGoingParentViewHolder holder, int position) {
        OnGoingOrderParentItems currentOnGoingOrderItem = onGoingOrderParentItems.get(position);
        holder.itemNo.setText(currentOnGoingOrderItem.getItemNum());
        holder.totalNo.setText(currentOnGoingOrderItem.getTotalCost());
        holder.orderId.setText(currentOnGoingOrderItem.getOrderId());
        holder.dateOrdered.setText(currentOnGoingOrderItem.getDateOrdered());

        onGoingOrderChildItems = new ArrayList<>();

        //initialize child recycler view
        HashMap<String,Object> itemLists = currentOnGoingOrderItem.getItemsList();

        for(HashMap.Entry<String, Object> entry : itemLists.entrySet())
        {
            String imgPath;
            String price;
            String title;
            String deliveredBy;

            Product p = findProduct(entry.getKey());
            Log.d("Entry key", entry.getKey());
            Log.d("Product Size", "onBindViewHolder: "+products.size());
            imgPath = p.getImgPath();
            price = String.valueOf((double)(p.price * Double.parseDouble(entry.getValue().toString())));
            String title1 = p.getTitle();
            String title2 = "";
            if(p.getType().equals("egg"))
            {
                title2 = "Egg";
            }

            if(p.getType().equals("meat"))
                title2 = "Chicken";

            title = title1 + title2;

            deliveredBy  = currentOnGoingOrderItem.getDeliverBy();

            //Adding Item to onGoingOrderChildItems list
            onGoingOrderChildItems.add(new OnGoingOrderChildItem(imgPath,price,title,deliveredBy));
        }

        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ongoingChildAdapter = new OngoingChildAdapter(onGoingOrderChildItems, context, OnGoingParentAdapter.this, position);
        holder.childRecyclerView.setAdapter(ongoingChildAdapter);
        ongoingChildAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return onGoingOrderParentItems.size();
    }

    @Override
    public void onTrackClick(int parentPosition) {
        onGoingParentListener.onChildTrackClicked(parentPosition);
    }

    public static class OnGoingParentViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNo;
        public TextView totalNo;
        public TextView orderId;
        public TextView dateOrdered;
        public RecyclerView childRecyclerView;
        OnGoingParentListener listener;

        public OnGoingParentViewHolder(@NonNull View itemView, OnGoingParentListener onGoingParentListener) {
            super(itemView);
            itemNo = itemView.findViewById(R.id.itemNoOnGoing);
            totalNo = itemView.findViewById(R.id.totalNoOnGoing);
            orderId = itemView.findViewById(R.id.orderIdOnGoing);
            dateOrdered = itemView.findViewById(R.id.placedDateOnGoing);
            childRecyclerView = itemView.findViewById(R.id.recyclerViewChild);
            listener = onGoingParentListener;
        }
    }

    public Product findProduct(String id){
        for(Product product:products){
            if(product.id.equals(id))
                return product;
        }
        return null;
    }

    public interface OnGoingParentListener{
        void onChildTrackClicked(int parentPosition);
    }
}
