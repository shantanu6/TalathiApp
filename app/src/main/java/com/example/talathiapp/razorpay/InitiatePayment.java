package com.example.talathiapp.razorpay;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.talathiapp.ApiKeys;
import com.example.talathiapp.R;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class InitiatePayment extends AsyncTask {
    Activity activity;
    Context context;
    String totalAmountinPaise,mobile;
    RazorpayClient razorpay;

    public InitiatePayment(Context context, String totalAmountinPaise,String mobile, RazorpayClient razorpay){
        this.activity=(Activity) context;
        this.context=context;
        this.totalAmountinPaise=totalAmountinPaise;
        this.razorpay=razorpay;
        this.mobile=mobile;
    }
    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            //Create Order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", totalAmountinPaise);
            orderRequest.put("currency", "INR");
            Order order = razorpay.Orders.create(orderRequest);
            JSONObject jsonOrder= new JSONObject(String.valueOf(order));

            final Checkout co = new Checkout();
            co.setKeyID(ApiKeys.getRzpKey());

            //Initiate Payment
            JSONObject options = new JSONObject();
            options.put("name", "Talathi");
            options.put("description", "Gav Allotment Fees");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            options.put("order_id", jsonOrder.getString("id"));//from response of step 3.
            //You can omit the image option to fetch the image from dashboard
            options.put("image", context.getString(R.string.payment_img_url));
            options.put("currency", "INR");
            options.put("amount", totalAmountinPaise);
            JSONObject preFill = new JSONObject();
            preFill.put("contact", mobile);
            options.put("prefill", preFill);

            /*JSONArray gaonDetails= new JSONArray();
            gaonDetails.put("GAV 1");
            gaonDetails.put("GAV 2");
            gaonDetails.put("GAV 3");
            options.put("notes", gaonDetails);*/

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("TAG", "onPostExecute: "+o);
    }
}
