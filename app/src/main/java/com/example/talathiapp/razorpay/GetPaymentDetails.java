package com.example.talathiapp.razorpay;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.talathiapp.ApiKeys;
import com.example.talathiapp.R;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import org.json.JSONObject;

public class GetPaymentDetails extends AsyncTask<Payment,Boolean,Payment> {
    String paymentId;
    Context context;
    String signature;
    RazorpayClient razorpay;
    VerificationSuccessfull verificationSuccessfull;
    public GetPaymentDetails(Context context, RazorpayClient razorpay, String paymentId, String signature){
        this.paymentId=paymentId;
        this.razorpay=razorpay;
        this.context=context;
        this.signature=signature;
        verificationSuccessfull= (VerificationSuccessfull) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Payment doInBackground(Payment... payments) {
        Payment payment = null;
        try {
            payment = razorpay.Payments.fetch(paymentId);
            return payment;
        } catch (RazorpayException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Payment payment) {
        super.onPostExecute(payment);
        try {
            JSONObject paymentJson = payment.toJson();
            Log.d("SACHIN",""+paymentJson);
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", paymentJson.getString("order_id"));
            options.put("razorpay_payment_id", paymentJson.getString("id"));
            options.put("razorpay_signature", signature);
            boolean paymentVerification = Utils.verifyPaymentSignature(options,ApiKeys.getRzpSecret());
            verificationSuccessfull.onVerificationSuccessfull(paymentVerification,payment);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
