package com.example.kadakadda.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class OnGoingOrderParentItems implements Parcelable {

    private String itemNum;
    private String totalCost;
    private String orderId;
    private String dateOrdered;
    private String deliverBy;
    private HashMap<String, Object> itemsList;

    public OnGoingOrderParentItems(String itemNum1, String totalCost1, String orderId1, String dateOrdered1, HashMap<String, Object> itemList1, String deliverBy1) {
        this.itemNum = itemNum1;
        this.totalCost = totalCost1;
        this.orderId = orderId1;
        this.dateOrdered = dateOrdered1;
        this.itemsList = itemList1;
        this.deliverBy = deliverBy1;
    }

    protected OnGoingOrderParentItems(Parcel in) {
        itemNum = in.readString();
        totalCost = in.readString();
        orderId = in.readString();
        dateOrdered = in.readString();
    }

    public static final Creator<OnGoingOrderParentItems> CREATOR = new Creator<OnGoingOrderParentItems>() {
        @Override
        public OnGoingOrderParentItems createFromParcel(Parcel in) {
            return new OnGoingOrderParentItems(in);
        }

        @Override
        public OnGoingOrderParentItems[] newArray(int size) {
            return new OnGoingOrderParentItems[size];
        }
    };

    public String getItemNum() {
        return itemNum;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public HashMap<String, Object> getItemsList() {
        return itemsList;
    }

    public String getDeliverBy() {
        return deliverBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemNum);
        dest.writeString(totalCost);
        dest.writeString(orderId);
        dest.writeString(dateOrdered);
    }
}
