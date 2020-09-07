package com.example.kadakadda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kadakadda.Models.API;
import com.example.kadakadda.Models.CartItem;
import com.example.kadakadda.Models.TokenRequest;
import com.example.kadakadda.Models.Token_Req;
import com.example.kadakadda.Models.Token_Res;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.paytm.pg.merchant.PaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentMessageActivity extends AppCompatActivity {

    private static final String TAG = "PaymentMessageActivity";
    private static final Integer ActivityRequestCode = 2;
    private static final String midString = "nLAxrS54480211652280";
    private static final String merchantKey = "ldRrc&dAc0L6BdQa";
    private static final String TOKEN_REQUEST_FULL_STRING = "https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid={mid}&orderId={order-id}";
    private static final String TOKEN_REQUEST = "https://securegw-stage.paytm.in/theia/api/v1/";
    //private static final String WEBSITE_NAME = "https://us-central1-kadakadda-testing.cloudfunctions.net/";
    private static final String WEBSITE_NAME = "WEBSTAGING";
    public static final String callBackUrl = "https://us-central1-kadakadda-e540a.cloudfunctions.net/PaymentCallback";
    private ProgressBar mProgressBar;
    private int mProgressStatus =0;
    private Handler mHandler = new Handler();
    private Retrofit retrofit;
    private OkHttpClient.Builder httpClient;
    private HttpLoggingInterceptor logging;
    private API api;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private ArrayList<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private double totalQuantity, totalCost;
    private String userUid, orderID, txnTokenString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_message);

        mProgressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        mFirestore = FirebaseFirestore.getInstance();

        cartItems = new ArrayList<>();
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_REQUEST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        api = retrofit.create(API.class);

        createOrderDocument();
    }

    //Create a new document, put transaction status as false
    public void createOrderDocument(){
        try{
            mProgressBar.setVisibility(View.VISIBLE);

            //Obtain Stored Cart JSON from SharedPreferences and convert to ArrayList
            String storedCart = sharedPreferences.getString(getString(R.string.user_cart), (new JSONObject()).toString());
            java.lang.reflect.Type type = new TypeToken<ArrayList<CartItem>>(){}.getType();
            cartItems =  gson.fromJson(storedCart, type);

            Log.i("PaymentMessage", "createOrderDocument: "+storedCart);
            Toast.makeText(this, "Local cart loaded.", Toast.LENGTH_SHORT).show();

            if(cartItems.size()==0)
                Log.i("PaymentMessage", "createOrderDocument: Empty Cart!");
            else{
                String selectedAddress = sharedPreferences.getString(getString(R.string.selectedAddress), "null");
                Date todayDate = new Date();
                Calendar today = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

                today.setTime(todayDate);
                today.add(Calendar.DAY_OF_YEAR, 1);
                Date nextDate = today.getTime();

                HashMap<String, Object> orderMap = new HashMap<>();

                HashMap<String, Double> cartMap = convertCartToHashMap();
                orderMap.put("deliveryaddress", selectedAddress);
                orderMap.put("delivered By", format.format(nextDate).toString());
                orderMap.put("items", cartMap);
                orderMap.put("itemNumber", String.valueOf(totalQuantity));
                orderMap.put("order_cancelled", false);
                orderMap.put("order_delivered", false);
                orderMap.put("order_outForDelivery", false);
                orderMap.put("order_processed", false);
                orderMap.put("timePlaced", todayDate);
                orderMap.put("transactionstatus", false);
                orderMap.put("total", String.valueOf(totalCost));
                orderMap.put("userID", userUid);

                DocumentReference docRef = mFirestore.collection("orders").document();
                orderID = docRef.getId();
                Log.d(TAG, "createOrderDocument: "+orderID);
                orderMap.put("orderID", orderID);
                docRef
                        .set(orderMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //startPaymentProcess();
                                    try {
                                        newStartPaymentProcess();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(PaymentMessageActivity.this, "Failed to create document. "+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*private void startPaymentProcess() {
        Token_Req req = new Token_Req(orderID, userUid, String.valueOf(totalCost));
        Call<Token_Res> call = api.getAppToken(req);
        call.enqueue(new Callback<Token_Res>() {
            @Override
            public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
                Log.e(TAG, " respo "+ response.isSuccessful() );
                mProgressBar.setVisibility(View.GONE);
                try {

                    if (response.isSuccessful() && response.body()!=null){
                        if (response.body().getBody().getTxnToken()!="") {
                            Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                            startPaytmPayment(response.body().getBody().getTxnToken());
                        }else {
                            Log.e(TAG, " Token status false");
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, " error in Token Res "+e.toString());
                }
            }

            @Override
            public void onFailure(Call<Token_Res> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, " response error "+t.toString());
            }
        });
    }*/

    public void startPaytmPayment (String token){

        txnTokenString = token;
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        //String host = "https://securegw.paytm.in/";
        //String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
          //      + ", Amount: " + txnAmountString;
        //Log.e(TAG, "order details "+ orderDetails);

        //String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderID;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderID, midString, txnTokenString, String.valueOf(totalCost), callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+s.toString());
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
            /*
                data response - {"BANKNAME":"WALLET","BANKTXNID":"1395841115",
                "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmMP5pH0hekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
                "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcR4116","ORDERID":"100620202152",
                "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS",
                "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"202006101112128001101683631290118"}
            */
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();

            clearCloudCart();

        }else{
            Log.e(TAG, " payment failed");
        }
    }

    private void clearCloudCart() {
        mFirestore.collection("user")
                .document(userUid)
                .update("cart", null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Cleared user cart.");
                        } else{
                            Log.d(TAG, "onComplete: Failed to clear user cart."+task.getException().getLocalizedMessage());
                        }
                        sendToConfirmationPage();
                    }
                });
    }

    private void sendToConfirmationPage() {
        //Send to Confimation page
        Intent intent = new Intent(PaymentMessageActivity.this, PaymentConfirmationActivity.class);
        startActivity(intent);
    }

    public HashMap<String, Double> convertCartToHashMap(){
        HashMap<String, Double> map = new HashMap<>();
        for(CartItem item : cartItems) {
            map.put(item.product.id, item.quantity);
            totalQuantity += item.quantity;
            totalCost +=item.totalCost;
        }
        return map;
    }

    //Fetches Token from PayTM Server
    public void newStartPaymentProcess() throws Exception {

        JSONObject paytmParams = new JSONObject();

        JSONObject body = new JSONObject();
        body.put("requestType", "Payment");
        body.put("mid", midString);
        body.put("websiteName", WEBSITE_NAME);
        body.put("orderId", orderID);
        //body.put("callbackUrl", "https://merchant.com/callback");

        JSONObject txnAmount = new JSONObject();
        txnAmount.put("value", "1.00");
        txnAmount.put("currency", "INR");

        JSONObject userInfo = new JSONObject();
        userInfo.put("custId", userUid);
        body.put("txnAmount", txnAmount);
        body.put("userInfo", userInfo);

        /*
         * Generate checksum by parameters we have in body
         * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */

        String checksum = PaytmChecksum.generateSignature(body.toString(), merchantKey);

        JSONObject head = new JSONObject();
        head.put("signature", checksum);

        paytmParams.put("body", body);
        paytmParams.put("head", head);

        String post_data = paytmParams.toString();

        /* for Staging */
        URL url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");

        /* for Production */
        // URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");

        try {
            TokenRequest request = new TokenRequest(orderID, "1.00", userUid);
            Call<Token_Res> call = api.getAppToken(request, midString, orderID);
            call.enqueue(new Callback<Token_Res>() {
                @Override
                public void onResponse(Call<Token_Res> call, Response<Token_Res> response) {
                    Log.e(TAG, " respo "+ response.isSuccessful() );
                    mProgressBar.setVisibility(View.GONE);
                    try {

                        if (response.isSuccessful() && response.body()!=null){
                            if (response.body().getBody().getTxnToken()!="") {
                                Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                                Toast.makeText(PaymentMessageActivity.this, "Got token.", Toast.LENGTH_SHORT).show();
                                startPaytmPayment(response.body().getBody().getTxnToken());
                            }else {
                                Toast.makeText(PaymentMessageActivity.this, "Not Got token.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, " Token status false");
                            }
                        }
                    }catch (Exception e){
                        Toast.makeText(PaymentMessageActivity.this, "Error in token res.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, " error in Token Res "+e.toString());
                    }
                }

                @Override
                public void onFailure(Call<Token_Res> call, Throwable t) {
                    mProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, " response error "+t.toString());
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}