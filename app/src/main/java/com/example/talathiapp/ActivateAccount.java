package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.talathiapp.razorpay.GetPaymentDetails;
import com.example.talathiapp.razorpay.InitiatePayment;
import com.example.talathiapp.razorpay.VerificationSuccessfull;
import com.google.android.gms.common.api.Api;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Payment;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivateAccount extends AppCompatActivity implements PaymentResultWithDataListener, VerificationSuccessfull {

    Spinner spnGav1, spnGav2, spnGav3, spnPlan;
    Button btnMakePay;
    ArrayAdapter<String> spnAdapter;
    ConstraintLayout ActivateAcc;
    String TAG = "Account";
    ArrayList<String> gavList = new ArrayList<>();
    ArrayList<String> planList = new ArrayList<>();
    JSONArray gavArray, planArray;
    JSONArray gavValues = new JSONArray();
    String uid,uname,mobile,tid,ename;
    static  String planId;

    ProgressDialog pd;
    RazorpayClient razorpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        getSupportActionBar().setTitle("Activate Account");
        spnGav1 = findViewById(R.id.spnGav1);
        spnGav2 = findViewById(R.id.spnGav2);
        spnGav3 = findViewById(R.id.spnGav3);
        spnPlan = findViewById(R.id.spnPlan);
        btnMakePay = findViewById(R.id.btnMakePay);
        ActivateAcc = findViewById(R.id.ActivateAcc);

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);

        uid=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("UID","");
        uname=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("UNAME","");
        ename=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("ENAME","");
        mobile=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("PHONE","");
        tid=getSharedPreferences("USER_DETAILS",MODE_PRIVATE).getString("TID","");

        get_tid_gavs(); // Get Gavs

        spnGav1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!adapterView.getSelectedItem().equals("Select Gav")) {
                    try {
                        gavValues.put(gavArray.getString(position).split("@")[1]);
                        gavArray.remove(position);
                        spnGav1.setEnabled(false);
                        set_gav(spnGav2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: ");
            }
        });

        spnGav2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!adapterView.getSelectedItem().equals("Select Gav")) {
                    try {
                        gavValues.put(gavArray.getString(position).split("@")[1]);
                        gavArray.remove(position);
                        spnGav2.setEnabled(false);
                        set_gav(spnGav3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnGav3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (!adapterView.getSelectedItem().equals("Select Gav")) {
                    try {
                        gavValues.put(gavArray.getString(position).split("@")[1]);
                        gavArray.remove(position);
                        spnGav3.setEnabled(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnMakePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // To close Virtual Keyboard
                InputMethodManager imm = (InputMethodManager) ActivateAccount.this.getSystemService(ActivateAccount.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnMakePay.getWindowToken(), 0);

                if (gavValues.length() != 0) {
                    try {
                        String orderId = UUID.randomUUID().toString().substring(0, 12);
                        String amount=planArray.getString(spnPlan.getSelectedItemPosition()).split("@",3)[2];
                        planId=planArray.getString(spnPlan.getSelectedItemPosition()).split("@",3)[0];
                        select_payment_mode(orderId,amount);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(ActivateAcc, "Please Select Atleast One Gav", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                }
            }
        });
    }

    void get_tid_gavs(){

        RequestQueue req = Volley.newRequestQueue(this);
        String url = getString(R.string.db_url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("SUCCESS").equals("1")) {
                        JSONObject jData = new JSONObject(jsonObject.getString("RESULT"));
                        gavArray = new JSONArray(jData.getString("GAVS"));
                        planArray = new JSONArray(jData.getString("PLANS"));
                        set_gav(spnGav1);
                        set_plans(spnPlan);
                        Snackbar.make(ActivateAcc, jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    } else {
                        Snackbar.make(ActivateAcc, jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String, String>();
                params.put("RESULT_TYPE", "GET_TID_GAVS");
                params.put("TID", tid);
                return params;
            }
        };

        req.add(stringRequest);
    }

    void select_payment_mode(String orderId, String amount){

        get_keys(); // Get Api keys

        AlertDialog.Builder alert = new AlertDialog.Builder(ActivateAccount.this);
        alert.setTitle("Please Select Payment Mode");
        alert.setMessage("");

        alert.setPositiveButton("Razorpay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                PaymentRazorpay( amount,   ActivateAccount.this);
            }
        });

        alert.setNeutralButton("Paytm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                PaymentPaytm( orderId,  amount);
            }
        });
        alert.show();
    }

    void get_keys(){

        RequestQueue req = Volley.newRequestQueue(this);
        String url = getString(R.string.db_url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("SUCCESS").equals("1")) {
                        JSONObject jData = new JSONObject(jsonObject.getString("RESULT"));
                        Log.d(TAG, "onResponse: "+jData);
                        // Set Api Keys

                        ApiKeys.setRzpKey(jData.getString("RZPKEY"));
                        ApiKeys.setRzpSecret(jData.getString("RZPSECRET"));
                        ApiKeys.setPytmMid(jData.getString("PYTMMID"));
                        ApiKeys.setPytmMkey(jData.getString("PYTMMKEY"));
                        ApiKeys.setFbKey(jData.getString("FBKEY"));

                        Log.d(TAG, "onResponse: "+ApiKeys.getRzpKey());

                        Snackbar.make(ActivateAcc, jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    } else {
                        Snackbar.make(ActivateAcc, jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String, String>();
                params.put("RESULT_TYPE", "GET_API_KEYS");
                return params;
            }
        };

        req.add(stringRequest);

    }

    void set_gav(Spinner spinItem) {
        gavList = new ArrayList<>();
        spnAdapter = new ArrayAdapter<String>(ActivateAccount.this, R.layout.spinner_item, gavList);
        try {
            for (int i = 0; i < gavArray.length(); i++) {
                gavList.add(gavArray.getString(i).split("@")[0]);
            }
            spinItem.setAdapter(spnAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void set_plans(Spinner spinItem) {
        planList = new ArrayList<>();
        spnAdapter = new ArrayAdapter<String>(ActivateAccount.this, R.layout.spinner_item, planList);
        try {
            for (int i = 0; i < planArray.length(); i++) {
                planList.add(planArray.getString(i).split("@",3)[1]);
            }
            spinItem.setAdapter(spnAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "set_plans: " + planArray);
    }


     void onTransactionSuccess(String USERID, String MOBILE, String TXNID, int TXNAMNT, String ORDERID, String STATUS, String MODE, ProgressDialog pdialog) throws UnsupportedEncodingException {
         try {
             RequestQueue requestQueue = Volley.newRequestQueue(this);
             String URL = getString(R.string.db_url);

             StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {

                     Log.d(TAG, "" + response);

                     try {
                         JSONObject jsonObject = new JSONObject(String.valueOf(response));
                         Log.d(TAG, "" + jsonObject);
                         int success = jsonObject.getInt("success");
                         String message = jsonObject.getString("message");

                         //  Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                         Snackbar snackbar = Snackbar
                                 .make(ActivateAcc, message, Snackbar.LENGTH_LONG);
                         if (success == 1) { // Payment Successfull
                             snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));
                             snackbar.show();
                             startActivity(new Intent(ActivateAccount.this,Dashboard.class));
                         } else
                             snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                         snackbar.show();

                     } catch (Exception e) {
                         e.printStackTrace();
                         Snackbar snackbar = Snackbar
                                 .make(ActivateAcc, "Payment Error", Snackbar.LENGTH_LONG);
                         snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                         snackbar.show();
                         Toast.makeText(ActivateAccount.this, "Payment Error:" + e, Toast.LENGTH_SHORT).show();

                     }


                 }
             }, new Response.ErrorListener() {

                 @Override
                 public void onErrorResponse(VolleyError error) {
                     Log.d(TAG, "" + error);

                 }

             }) {
                 @Override
                 protected Map<String, String> getParams() throws AuthFailureError {

                     Map<String, String> params = new HashMap<>();
                     params.put("USERID", USERID);
                     params.put("MOBILE", MOBILE);
                     params.put("TXNID", TXNID);
                     params.put("TXNAMNT", String.valueOf(TXNAMNT));
                     params.put("ORDERID", ORDERID);
                     params.put("STATUS", STATUS);
                     params.put("MODE", MODE);
                     params.put("PLANID", planId);
                     params.put("GAV_DETAILS", gavValues.toString());
                     params.put("RESULT_TYPE", "INSERT_PAYMENT");
                     return params;
                 }
             };
             requestQueue.add(stringRequest);
         } catch (Exception e) {
             e.printStackTrace();
         }

     }


    void PaymentPaytm(String orederId, String totalAmount ) {
        ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading..");
        pdialog.setTitle("Please Wait");
        pdialog.show();

        // paymentMethodDialog.dismiss();
        // getQuantityIds = false;
        if (ContextCompat.checkSelfPermission(ActivateAccount.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivateAccount.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

//        final String MID = "jcHDRT48790353839348";

        final String MID =ApiKeys.getPytmMid();
        final String customerId =uid;
        String url = getString(R.string.paytm_checksum_url);
        final String requestUrl="https://securegw-stage.paytm.in/order/process";
        final String callbackUrl="https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orederId;
        RequestQueue requestQueue = Volley.newRequestQueue(ActivateAccount.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                pdialog.dismiss();
                Log.d("PAYTM",""+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH")) {
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
                        PaytmPGService paytmPGService = PaytmPGService.getStagingService(requestUrl);
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        paramMap.put("MID", MID);
                        paramMap.put("ORDER_ID", orederId);
                        paramMap.put("CUST_ID", customerId);
                        paramMap.put("CHANNEL_ID", "WAP");
                        paramMap.put("TXN_AMOUNT", totalAmount);
                        paramMap.put("WEBSITE", "WEBSTAGING");
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                        paramMap.put("CALLBACK_URL", callbackUrl);
                        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);

                        PaytmOrder paytmOrder = new PaytmOrder(paramMap);
                        paytmPGService.initialize(paytmOrder, null);
                        paytmPGService.startPaymentTransaction(ActivateAccount.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {

                                Log.d("PAYTM",""+inResponse);

                                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                    //Transaction Successfull
                                    try {
                                        //Transaction Successfull
                                        Log.d("PAYMENT", "Payment Successfull: ");
                                        String USERID=ename;
                                        String MOBILE=mobile;
                                        String TXNID =inResponse.getString("TXNID");
                                        int TXNAMNT= (int)Double.parseDouble(inResponse.getString("TXNAMOUNT"));
                                        String ORDERID=inResponse.getString("ORDERID");
                                        String STATUS=inResponse.getString("STATUS");
                                        String MODE="paytm";
                                        try {
                                            onTransactionSuccess( USERID,MOBILE,TXNID,TXNAMNT,ORDERID,STATUS,MODE,pd);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Snackbar snackbar = Snackbar
                                                .make(ActivateAcc, "Payment Error", Snackbar.LENGTH_LONG);
                                        snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                                    }
                                }
                                // Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void networkNotAvailable() {

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Check Network Connection", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }

                            @Override
                            public void onErrorProceed(String s) {

                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Server Error", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Internal Error", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Unable to load Payment Page", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Transaction Cancelled", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();

                                pdialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(ActivateAcc, "Transaction Cancelled", Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdialog.dismiss();
                // Toast.makeText(ActivateAccount.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar
                        .make(ActivateAcc, "Something Went Wrong", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(ContextCompat.getColor(ActivateAccount.this, R.color.colorPrimary));

                Log.d("ERROR",""+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("MID", MID);
                paramMap.put("ORDER_ID", orederId);
                paramMap.put("CUST_ID", customerId);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", totalAmount);
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                paramMap.put("CALLBACK_URL", callbackUrl);
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);

    }

    void PaymentRazorpay(String totalAmount,  Context context) {

        pd = new ProgressDialog(context);
        pd.setTitle("Processing Payment");
        pd.setMessage("Please Wait");
        pd.setCancelable(false);
        pd.show();
        try {
            String totalAmountinPaise = String.valueOf(Integer.parseInt(totalAmount) * 100);
            razorpay = new RazorpayClient(ApiKeys.getRzpKey(), ApiKeys.getRzpSecret());
            new InitiatePayment(context, totalAmountinPaise, mobile, razorpay).execute();
        } catch (RazorpayException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        new GetPaymentDetails(ActivateAccount.this, razorpay, paymentData.getPaymentId(), paymentData.getSignature()).execute();
        Log.d("SACHIN", ": " + paymentData);
        pd.dismiss();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("SACHIN", ": " + paymentData);
        pd.dismiss();
    }

    @Override
    public void onVerificationSuccessfull(Boolean status, Payment payment) {
        Log.d("SACHIN", "Status: " + status + " Payment:  " + payment);
        String USERID = ename;
        String MOBILE = payment.get("contact");
        String TXNID = payment.get("id");
        int TXNAMNT = payment.get("amount");
        TXNAMNT = TXNAMNT / 100;
        String ORDERID = payment.get("order_id");
        String STATUS = payment.get("status");
        String MODE = "razorpay";
        try {
            onTransactionSuccess(USERID, MOBILE, TXNID, TXNAMNT, ORDERID, STATUS, MODE, pd);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pd.dismiss();
    }

}

