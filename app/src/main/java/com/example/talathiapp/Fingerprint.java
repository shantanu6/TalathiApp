package com.example.talathiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;

import java.util.concurrent.Executor;

public class Fingerprint extends AppCompatActivity   {

    static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        getSupportActionBar().hide();

        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {

            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
//                Toast.makeText(getApplicationContext(), "You can use the fingerprint sensor to login", Toast.LENGTH_SHORT).show();
                 break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                Toast.makeText(getApplicationContext(), "This device doesn't have a fingerprint sensor", Toast.LENGTH_SHORT).show();
                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                Toast.makeText(this, "The biometric sensor is currently unavailable", Toast.LENGTH_SHORT).show();
                break;

            // this means that the device doesn't contain your fingerprint or not set any password
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Your device doesn't have fingerprint saved or not set any password,please check your security settings", Toast.LENGTH_LONG).show();
                break;
        }
        // creating a variable for our Executor
        Executor executor = ContextCompat.getMainExecutor(this);
        // this will give us result of AUTHENTICATION
       final BiometricPrompt biometricPrompt = new BiometricPrompt(Fingerprint.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "onAuthenticationError", Toast.LENGTH_SHORT).show();
                finish();
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "onAuthenticationSuccess", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Fingerprint.this, Dashboard.class));
                finish();
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                if(count==3){
                    Toast.makeText(getApplicationContext(), "onAuthenticationFailed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                count++;

            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Authentication")
                .setDescription("Use your fingerprint or device pin to login ")
                .setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);


    }

}