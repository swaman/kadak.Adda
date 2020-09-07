package com.example.kadakadda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Models.OnGoingOrderChildItem;
import com.example.kadakadda.R;
import com.example.kadakadda.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OngoingChildAdapter extends RecyclerView.Adapter<OngoingChildAdapter.ChildViewHolder> {

    ArrayList<OnGoingOrderChildItem> onGoingOrderChildItems = new ArrayList<>();
    Context mContext;
    OnGoingChildListener onGoingChildListener;
    int parentPosition;

    public OngoingChildAdapter(ArrayList<OnGoingOrderChildItem> onGoingOrderChildItems, Context context, OnGoingChildListener listener, int position) {
        this.onGoingOrderChildItems = onGoingOrderChildItems;
        mContext = context;
        onGoingChildListener = listener;
        parentPosition = position;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row,parent,false);
        return new ChildViewHolder(view, onGoingChildListener, parentPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildViewHolder holder, int position) {
        OnGoingOrderChildItem currentChildItem = onGoingOrderChildItems.get(position);
        Picasso.get().load(Uri.parse(currentChildItem.getImagePath())).into(holder.itemImage);
        holder.price.setText(currentChildItem.getPrice());
        holder.title.setText(currentChildItem.getTitle());
        holder.deliveryBy.setText(currentChildItem.getDeliveredBy());
    }

    @Override
    public int getItemCount() {
        return onGoingOrderChildItems.size();
    }


    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView price;
        public TextView title;
        public TextView deliveryBy;
        public TextView track;
        int parentPosition;
        OnGoingChildListener listener;

        public ChildViewHolder(@NonNull View itemView, OnGoingChildListener onGoingChildListener, int position) {
            super(itemView);
            listener = onGoingChildListener;
            itemImage = itemView.findViewById(R.id.ItemImage);
            price = itemView.findViewById(R.id.itemPrice);
            title = itemView.findViewById(R.id.itemName);
            deliveryBy = itemView.findViewById(R.id.itemDeliveryDate);
            track = itemView.findViewById(R.id.track);
            this.parentPosition = position;

            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTrackClick(parentPosition);
                }
            });

        }
    }

    public interface OnGoingChildListener{
        void onTrackClick(int parentPosition);
    }
}
