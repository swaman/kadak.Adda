package com.example.kadakadda.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kadakadda.Models.PastOrderItems;
import com.example.kadakadda.R;

import java.util.ArrayList;

public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.PastOrderViewHolder> {

    private ArrayList<PastOrderItems> pastOrderItems;

    public PastOrderAdapter(ArrayList<PastOrderItems> pastOrderItems) {
        this.pastOrderItems = pastOrderItems;
    }

    @NonNull
    @Override
    public PastOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.pastorder_row,parent,false);

        return new PastOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastOrderViewHolder holder, int position) {
        PastOrderItems currentOrderItem = pastOrderItems.get(position);
        holder.itemNo.setText(currentOrderItem.getItemNum());
        holder.totalNo.setText(currentOrderItem.getTotalCost());
        holder.orderId.setText(currentOrderItem.getOrderId());
        holder.dateOrdered.setText(currentOrderItem.getDateOrdered());


    }

    @Override
    public int getItemCount() {
        return pastOrderItems.size();
    }

    public static class PastOrderViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNo;
        public TextView totalNo;
        public TextView orderId;
        public TextView dateOrdered;
        public PastOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNo = itemView.findViewById(R.id.itemNo);
            totalNo = itemView.findViewById(R.id.totalNo);
            orderId = itemView.findViewById(R.id.orderId);
            dateOrdered = itemView.findViewById(R.id.placedDate);


        }
    }

}
