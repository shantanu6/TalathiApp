package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordToken extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout UpdatePasswordActivity;
    EditText oldMobile,oldSevarthId,oldFullName,newPass,confPass;
    Button btnverify,btnUpdateInfo;
    static String RESULT;
    LinearLayout newPassLinear,confPassLinear;
    ProgressDialog progressDialog;
    String TAG="Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password_token);
        UpdatePasswordActivity=findViewById(R.id.UpdatePasswordActivity);
        oldMobile=findViewById(R.id.oldMobile);
        oldSevarthId=findViewById(R.id.oldSevarthId);
        oldFullName=findViewById(R.id.oldFullName);
        newPass=findViewById(R.id.newPass);
        confPass=findViewById(R.id.confPass);
        btnverify=findViewById(R.id.btnverify);
        btnUpdateInfo=findViewById(R.id.btnUpdateInfo);
        newPassLinear=findViewById(R.id.newPassLinear);
        confPassLinear=findViewById(R.id.confPassLinear);

        RESULT=getIntent().getStringExtra("RESULT");

        if(RESULT.equals("PASSWORD")){
            getSupportActionBar().setTitle("Update Password");
        }else if(RESULT.equals("FORGET_PASS")){
            getSupportActionBar().setTitle("Forget Password");
        }else {
            getSupportActionBar().setTitle("Update Device");
        }

        btnverify.setOnClickListener(this);
        btnUpdateInfo.setOnClickListener(this);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching Data");
        progressDialog.setMessage("Please Wait");

    }


    void getRecords(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE, String RESULT_TYPE){
        progressDialog.show();
        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
//                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));
                        Log.d(TAG, "onResponse: "+result);
                        setRecords(result,RESULT_TYPE);

                        //   Snackbar.make(UpdatePasswordActivity,result.getJSONObject.get("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(UpdatePasswordActivity, jsonObject.getString("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                        newPassLinear.setVisibility(View.GONE);
                        confPassLinear.setVisibility(View.GONE);
                        btnverify.setVisibility(View.VISIBLE);
                        btnUpdateInfo.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: "+error.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String,String>();
                params.put("RESULT_TYPE", REQUEST_TYPE);
                params.put("TABLE", TABLE);
                params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                return params;
            }
        };

        req.add(stringRequest);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnverify:
                hideKeyboard(this);
                verifyDetails();
                break;
            case R.id.btnUpdateInfo:
                hideKeyboard(this);
                updateData();
                break;
        }
    }


    void verifyDetails(){
        if(!oldMobile.getText().toString().equals("") && !oldSevarthId.getText().toString().equals("") && !oldFullName.getText().toString().equals("")){
            try {
                String whereclause = " WHERE mobileno='"+oldMobile.getText().toString()+"' AND sevarth='"+oldSevarthId.getText().toString()+"' AND full_name='"+oldFullName.getText().toString()+"' ";
                getRecords(UpdatePasswordToken.this, "GET_RECORDS",  "user_verification",whereclause,"VERIFY_DETAILS");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show();
        }

    }

    void setRecords(JSONArray result , String RESULT_TYPE){
        try {
            switch (RESULT_TYPE){
                case "VERIFY_DETAILS":
                    oldMobile.setEnabled(false);
                    oldSevarthId.setEnabled(false);
                    oldFullName.setEnabled(false);
                    Snackbar.make(UpdatePasswordActivity,"Verification Succssfull", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();

                     if(RESULT.equals("PASSWORD") || RESULT.equals("FORGET_PASS")){
                        newPassLinear.setVisibility(View.VISIBLE);
                        confPassLinear.setVisibility(View.VISIBLE);
                    }else {
                        newPassLinear.setVisibility(View.GONE);
                        confPassLinear.setVisibility(View.GONE);
                    }

                    btnverify.setVisibility(View.GONE);
                    btnUpdateInfo.setVisibility(View.VISIBLE);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void updateData(){
        if(RESULT.equals("PASSWORD") || RESULT.equals("FORGET_PASS") ){
            if(!newPass.getText().toString().equals("") && !confPass.getText().toString().equals("")  ){
                if( newPass.getText().toString().equals(confPass.getText().toString())){
                    JSONObject jsonObject= new JSONObject();
                    try {
                        jsonObject.put("password",confPass.getText().toString());
                        updatePassToken(UpdatePasswordToken.this,"UPDATE_DETAILS",jsonObject,"users","WHERE  username='"+oldMobile.getText().toString()+"' ","Password Update Successful");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Snackbar.make(UpdatePasswordActivity,"Password Mismatch", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                }
            }else{
                Toast.makeText(this, "Enter New Password / Confirm Password", Toast.LENGTH_SHORT).show();
            }
        }else {
            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("token","0");
                updatePassToken(UpdatePasswordToken.this,"UPDATE_DETAILS",jsonObject,"users","WHERE  username='"+oldMobile.getText().toString()+"' ","Handset Request Update Successful");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public void updatePassToken(Context context,String REQUEST_TYPE, JSONObject DATA, String TABLE, String WHERE_CLAUSE,String MESSAGE)  {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = getString(R.string.db_url);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getInt("success")==1){
                            Toast.makeText(context, ""+MESSAGE, Toast.LENGTH_LONG).show();
                            switch (RESULT){
                                case "PASSWORD":
                                    startActivity(new Intent(context,UserProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    break;

                                case "HANDSET_TOKEN":
                                    startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    break;

                                case "FORGET_PASS":
                                    startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    break;
                            }

                        }else{
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                }}, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "" + error);

                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("DATA",DATA.toString());
                    params.put("TABLE", TABLE);
                    params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                    params.put("RESULT_TYPE", REQUEST_TYPE);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public  void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}