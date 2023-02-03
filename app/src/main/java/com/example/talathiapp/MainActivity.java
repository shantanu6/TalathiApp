package com.example.talathiapp;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    EditText uname,pass;
    Button btnLogin;
    String TAG="Login";
    View loginActivity;
    LinearLayout regLink,changeDevLink,forgetPassLink;
    static String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uname=findViewById(R.id.uname);
        pass=findViewById(R.id.pass);
        btnLogin=findViewById(R.id.btnLogin);
        loginActivity = findViewById(R.id.loginActivity);
        regLink = findViewById(R.id.regLink);
        changeDevLink = findViewById(R.id.changeDevLink);
        forgetPassLink = findViewById(R.id.forgetPassLink);

        regLink.setOnClickListener(this);
        changeDevLink.setOnClickListener(this);
        forgetPassLink.setOnClickListener(this);

        getSupportActionBar().setTitle("Talathi App Office");

            askPermission();

        SharedPreferences user_details = getSharedPreferences("USER_DETAILS",MODE_PRIVATE);
        SharedPreferences.Editor user_edit=user_details.edit();

        if(user_details.getString("ISLOGIN","0").equals("1")){
            startActivity(new Intent(MainActivity.this,Fingerprint.class));
            finish();
        }else {
            loginActivity.setVisibility(View.VISIBLE);
        }

        deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

        RequestQueue req = Volley.newRequestQueue(this);
        String url =getString(R.string.db_url);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // To close Virtual Keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);

                if(!uname.getText().toString().equals("") && !pass.getText().toString().equals("") ){

                    if(pass.getText().length()<=10){

                   StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: "+response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("SUCCESS").equals("1")){

                                    JSONObject result = new JSONObject(jsonObject.getString("RESULT"));
//                                    Log.d(TAG, "onResponse: "+result);
                                    user_edit.putString("ISLOGIN","1");
                                    user_edit.putString("UID",result.getString("UID"));
                                    user_edit.putString("UNAME",result.getString("UNAME"));
                                    user_edit.putString("ENAME",result.getString("ENAME"));
                                    user_edit.putString("PHONE",result.getString("PHONE"));
                                    user_edit.putString("TID",result.getString("TID"));
                                    user_edit.commit();

                                    genrateFcmToken(result.getString("UID"));

                                    startActivity(new Intent(MainActivity.this,Fingerprint.class));
                                    finish();

                                    Snackbar.make(loginActivity,jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                                }else {
                                    Snackbar.make(loginActivity, jsonObject.getString("MESSAGE"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onError: "+error);
                        }
                    }) {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap params = new HashMap<String,String>();
                            params.put("RESULT_TYPE","USER_LOGIN");
                            params.put("UNAME",uname.getText().toString());
                            params.put("PASS",pass.getText().toString());
                            params.put("TOKEN",deviceId);
                            return params;
                        }
                    };

                    req.add(stringRequest);

                    }else {
                        Snackbar.make(loginActivity,"Password Contains Max 10 Characters.", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }

                }else{
                    Snackbar.make(findViewById(R.id.loginActivity),"Please Fill All Fields", BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regLink:
                startActivity(new Intent(MainActivity.this,Register.class));
                break;

            case R.id.changeDevLink:
                Intent i1=new Intent(MainActivity.this, UpdatePasswordToken.class);
                i1.putExtra("RESULT","HANDSET_TOKEN");
                startActivity(i1);
                break;

            case R.id.forgetPassLink:
                Intent i2=new Intent(MainActivity.this, UpdatePasswordToken.class);
                i2.putExtra("RESULT","FORGET_PASS");
                startActivity(i2);
                break;
        }
    }

    void askPermission(){

      if (SDK_INT >= Build.VERSION_CODES.M) {

            new AskPermission.Builder(MainActivity.this)
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    .setCallback(new PermissionCallback() {
                        @Override
                        public void onPermissionsGranted(int requestCode) {
                            Log.d(TAG, "onPermissionsGranted: ");
//                            Toast.makeText(MainActivity.this, "onPermissionsGranted", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onPermissionsDenied(int requestCode) {
                            Log.d(TAG, "onPermissionsDenied: ");
//                            Toast.makeText(MainActivity.this, "onPermissionsDenied", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setErrorCallback(new ErrorCallback() {
                        @Override
                        public void onShowRationalDialog(PermissionInterface permissionInterface, int requestCode) {
                            Log.d(TAG, "onShowRationalDialog: ");
//                            Toast.makeText(MainActivity.this, "onShowRationalDialog", Toast.LENGTH_SHORT).show();
                            permissionInterface.onDialogShown();
                        }

                        @Override
                        public void onShowSettings(PermissionInterface permissionInterface, int requestCode) {
                            Log.d(TAG, "onShowSettings: " + permissionInterface.toString());
//                            Toast.makeText(MainActivity.this, "onShowSettings", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .request(100);
        }

    }


    void genrateFcmToken(String uid){

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                updateFCMToken( MainActivity.this, token, uid,"Login Success");
                Log.d(TAG, ""+token);
//                Toast.makeText(MainActivity.this,token,Toast.LENGTH_LONG);
            }
        });
    }

    public void updateFCMToken(  Context context, String fcmtoken, String uid, String message) {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("fcmtoken",fcmtoken);
            String whereclause="WHERE id='"+uid+"'";

            new ViewDetails().setServiceDetails(context,"UPDATE_DETAILS",jsonObject,"users", whereclause,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}