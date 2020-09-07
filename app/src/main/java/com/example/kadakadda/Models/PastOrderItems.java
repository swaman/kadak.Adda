package com.example.kadakadda.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class PastOrderItems implements Parcelable {

    private String itemNum;
    private String totalCost;
    private String orderId;
    private String dateOrdered;

    public PastOrderItems(String itemNum, String totalCost, String orderId, String dateOrdered) {
        this.itemNum = itemNum;
        this.totalCost = totalCost;
        this.orderId = orderId;
        this.dateOrdered = dateOrdered;
    }

    protected PastOrderItems(Parcel in) {
        itemNum = in.readString();
        totalCost = in.readString();
        orderId = in.readString();
        dateOrdered = in.readString();
    }

    public static final Creator<PastOrderItems> CREATOR = new Creator<PastOrderItems>() {
        @Override
        public PastOrderItems createFromParcel(Parcel in) {
            return new PastOrderItems(in);
        }

        @Override
        public PastOrderItems[] newArray(int size) {
            return new PastOrderItems[size];
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
