package com.reactnativegooglepayindia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class GooglePayIndiaModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    // Google Pay Variables
    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;

    private static final String ACTIVITY_DOES_NOT_EXIST = "ACTIVITY_DOES_NOT_EXIST";
    private static final String APPLICATION_NOT_PRESENT = "APPLICATION_NOT_PRESENT";
    private static final String INSUFFICIENT_DATA_FOR_PAYMENT = "INSUFFICIENT_DATA_FOR_PAYMENT";
    private static final String INTERNET_NOT_AVAILABLE = "INTERNET_NOT_AVAILABLE";
    private static final String TRANSACTION_FAILED = "TRANSACTION_FAILED";
    private static final String PAYMENT_CANCELLED_BY_USER = "PAYMENT_CANCELLED_BY_USER";



    private Promise rnGooglePayPromise;
    private ReadableMap rnGooglePayObject;

    private final ActivityEventListener rnActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
                // Process based on the data in response.
                Log.d("result", data.getStringExtra("Status"));
                Log.d("result", data.getStringExtra("response"));

                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            }
        }
    };


    GooglePayIndiaModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;

        // Add the listener for `onActivityResult`
        reactContext.addActivityEventListener(rnActivityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return "GooglePayIndia";
    }

    @ReactMethod
    public void startPayment(ReadableMap googlePayObject, final Promise promise) {

        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        // Store the promise to resolve/reject when picker returns data
        rnGooglePayPromise = promise;
        rnGooglePayObject = googlePayObject;

        // Checking if the application is there in the device
        if (!isPackageInstalled(GOOGLE_PAY_PACKAGE_NAME, currentActivity.getPackageManager())) {
            promise.reject(APPLICATION_NOT_PRESENT, "Application Not Present.");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user"));
            currentActivity.startActivity(browserIntent);
        } else {

            if (!googlePayObject.hasKey("merchantUpiId") || googlePayObject.getString("merchantUpiId").equalsIgnoreCase("")) {
                promise.reject(INSUFFICIENT_DATA_FOR_PAYMENT, "Merchant UPI ID invalid.");
            } else if (!googlePayObject.hasKey("merchantName") || googlePayObject.getString("merchantName").equalsIgnoreCase("")) {
                promise.reject(INSUFFICIENT_DATA_FOR_PAYMENT, "Merchant name invalid.");
            }  else if (!googlePayObject.hasKey("transactionNote") || googlePayObject.getString("transactionNote").equalsIgnoreCase("")) {
                promise.reject(INSUFFICIENT_DATA_FOR_PAYMENT, "Transaction note invalid.");
            } else if (!googlePayObject.hasKey("orderAmount") || googlePayObject.getString("orderAmount").equalsIgnoreCase("")) {
                promise.reject(INSUFFICIENT_DATA_FOR_PAYMENT, "Order amount invalid.");
            } else if (!googlePayObject.hasKey("transactionUrl") || googlePayObject.getString("transactionUrl").equalsIgnoreCase("")) {
                promise.reject(INSUFFICIENT_DATA_FOR_PAYMENT, "Transaction url invalid.");
            } else {
                completePayment();
            }
        }
    }

    void completePayment(){

        Activity currentActivity = getCurrentActivity();


        Uri.Builder builder = new Uri.Builder();

        builder.scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", rnGooglePayObject.getString("merchantUpiId"))
                .appendQueryParameter("pn", rnGooglePayObject.getString("merchantName"))
                .appendQueryParameter("tr", giveRandomString())
                .appendQueryParameter("tn", rnGooglePayObject.getString("transactionNote"))
                .appendQueryParameter("am", rnGooglePayObject.getString("orderAmount"))
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("url", rnGooglePayObject.getString("transactionUrl"));

        if(rnGooglePayObject.hasKey("merchantCode") && !rnGooglePayObject.getString("merchantCode").equalsIgnoreCase(""))
            builder.appendQueryParameter("mc", rnGooglePayObject.getString("merchantCode"));

        if(rnGooglePayObject.hasKey("transactionRefId") && !rnGooglePayObject.getString("transactionRefId").equalsIgnoreCase(""))
            builder.appendQueryParameter("tr", rnGooglePayObject.getString("transactionRefId"));

        Uri uri = builder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        currentActivity.startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {

        if (isConnectionAvailable(getCurrentActivity())) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String transactionId = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    } else if(equalStr[0].toLowerCase().equals("txnId".toLowerCase())){
                        transactionId = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                //Toast.makeText(CheckoutActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo+" "+transactionId);

                WritableMap paymentSuccessObject = Arguments.createMap();
                paymentSuccessObject.putString("approvalRefNo",approvalRefNo);
                paymentSuccessObject.putString("transactionId",transactionId);

                rnGooglePayPromise.resolve(paymentSuccessObject);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                //Toast.makeText(CheckoutActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
                rnGooglePayPromise.reject(PAYMENT_CANCELLED_BY_USER, "Transaction failed. Please try again.");

            }
            else {
                //Toast.makeText(CheckoutActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
                rnGooglePayPromise.reject(TRANSACTION_FAILED, "Transaction failed. Please try again.");
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            //Toast.makeText(CheckoutActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            rnGooglePayPromise.reject(INTERNET_NOT_AVAILABLE, "Internet is not available.  Please try again after connecting in to the internet.");
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    String giveRandomString() {
        byte[] array = new byte[12]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }


}
