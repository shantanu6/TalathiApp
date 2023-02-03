package com.example.talathiapp.razorpay;

import com.razorpay.Payment;

public interface VerificationSuccessfull {
    void onVerificationSuccessfull(Boolean status, Payment payment);
}
