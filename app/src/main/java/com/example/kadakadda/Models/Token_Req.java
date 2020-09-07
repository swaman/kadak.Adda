package com.example.kadakadda.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token_Req {

    @SerializedName("orderID")
    @Expose
    public String orderID;

    @SerializedName("userID")
    @Expose
    public String userID;

    @SerializedName("txnAmount")
    @Expose
    public String txnAmount;

    public Token_Req(String orderID, String userID, String txnAmount) {
        this.orderID = orderID;
        this.userID = userID;
        this.txnAmount = txnAmount;
    }
}
