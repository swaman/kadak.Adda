package com.example.kadakadda.Models;

import com.example.kadakadda.PaytmChecksum;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    public static final String midString = "nLAxrS54480211652280";
    public static final String merchantKey = "ldRrc&dAc0L6BdQa";
    public static final String WEBSITE_NAME = "WEBSTAGING";
    public static final String REQUEST_TYPE = "PAYMENT";
    @SerializedName("head")
    @Expose
    TokenHead head;
    @SerializedName("body")
    @Expose
    TokenBody body;

    public TokenRequest(String orderId, String value, String custId) throws Exception {
        //head = new TokenHead(signature);
        body = new TokenBody(orderId, value, custId);
        Gson gson = new Gson();
        String bodyJson = gson.toJson(body);
        String checksum = PaytmChecksum.generateSignature(bodyJson, merchantKey);
        head = new TokenHead(checksum);
    }
}
class TokenBody{
    @SerializedName("requestType")
    @Expose
    String requestType;
    @SerializedName("mid")
    @Expose
    String mid;
    @SerializedName("websiteName")
    @Expose
    String websiteName;
    @SerializedName("orderId")
    @Expose
    String orderId;
    @SerializedName("txnAmount")
    @Expose
    TokenTxnAmount txnAmount;
    @SerializedName("userInfo")
    @Expose
    TokenUserInfo userInfo;

    public TokenBody(String orderId, String value, String custId){
        this.requestType = TokenRequest.REQUEST_TYPE;
        this.mid = TokenRequest.midString;
        this.websiteName = TokenRequest.WEBSITE_NAME;
        this.orderId = orderId;
        this.txnAmount = new TokenTxnAmount(value);
        this.userInfo = new TokenUserInfo(custId);
    }
}
class TokenTxnAmount{
    @SerializedName("value")
    @Expose
    String value;
    @SerializedName("currency")
    @Expose
    String currency;

    public TokenTxnAmount(String value) {
        this.value = value;
        this.currency = "INR";
    }
}
class TokenUserInfo{
    @SerializedName("custId")
    @Expose
    String custId;

    public TokenUserInfo(String custId) {
        this.custId = custId;
    }
}
class TokenHead{
    @SerializedName("signature")
    @Expose
    String signature;

    public TokenHead(String signature) {
        this.signature = signature;
    }
}