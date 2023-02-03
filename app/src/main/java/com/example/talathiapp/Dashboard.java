package com.example.talathiapp;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.Constants;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity implements View.OnClickListener,UpdateManager.UpdateInfoListener  {

    RecyclerView dashboardRecyclerView;
    ConstraintLayout dashboard;
    Button btnActivate;
    ImageView btnLogOut;
    ArrayList<GavItemModel> itemModel;
    static String uname;
    String TAG="dashboard";
    private long pressedTime;
    RequestQueue req;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dashboard = findViewById(R.id.dashboard);
        dashboardRecyclerView = findViewById(R.id.dashboardRecyclerView);
        btnActivate = findViewById(R.id.btnActivate);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        dashboardRecyclerView.setLayoutManager(lm);

        req = Volley.newRequestQueue(this);
        url =getString(R.string.db_url);

        // Check Is Valid Token Exists
        CheckToken ct = new CheckToken();
        ct.isExitToken(this);

        checkUpdate();

        SharedPreferences user_details = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        uname = user_details.getString("UNAME", "");

        if(isMyServiceRunning(ForegroundService.class)) {
//            Toast.makeText(this, "Service Is Already Running", Toast.LENGTH_SHORT).show();
        }else{
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }

        // get inbox count for notification
        try {
            String whereclause = " WHERE status=0 AND username='"+uname+"' ";
            getRecords(this, "GET_RECORDS",  "servicerequest",whereclause);
        }catch (Exception e){
            e.printStackTrace();
        }

        getGav();

        btnActivate.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.activity_custom_action_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnActivate:
                Intent i1=new Intent(Dashboard.this,ActivateAccount.class);
                startActivity(i1);
                break;
            case R.id.btnLogOut:

                // Remove Sticky notification
                Intent stopIntent = new Intent(Dashboard.this, ForegroundService.class);
                getApplicationContext().stopService(stopIntent);

                getSharedPreferences("USER_DETAILS",MODE_PRIVATE).edit().clear().commit();
                updateCustomLayout(0);
                startActivity(new Intent(Dashboard.this,MainActivity.class));
                finish();
                break;
        }
    }

    void getGav(){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("SUCCESS").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("RESULT"));
                        itemModel=new ArrayList<>();
                        for(int i=0;i<result.length();i++){
                            Log.d(TAG, "onResponse: "+result.getJSONObject(i).getString("GCODE"));

                            GavItemModel im=new GavItemModel();
                            im.setGavName(result.getJSONObject(i).getString("GNAME"));
                            im.setGavCode(result.getJSONObject(i).getString("GCODE"));
                            im.setGavCount(result.getJSONObject(i).getString("INBOX"));
                            itemModel.add(im);
                        }
                        DashboardRecyclerView recycleAdapter=new DashboardRecyclerView(Dashboard.this,itemModel,dashboardRecyclerView);
                        dashboardRecyclerView.setAdapter(recycleAdapter);

                        btnActivate.setText("Allocate New Gav");
                        Snackbar.make(dashboard,jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        btnActivate.setText("Activate Account");
                        Snackbar.make(dashboard, jsonObject.getString("MESSAGE"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
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
                params.put("RESULT_TYPE","GET_GAV");
                params.put("UNAME",uname);
                return params;
            }
        };

        req.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(Dashboard.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    void checkUpdate(){
        //Check for Update
        UpdateManager mUpdateManager;
        mUpdateManager = UpdateManager.Builder(Dashboard.this).mode(UpdateManagerConstant.IMMEDIATE);
        mUpdateManager.addUpdateInfoListener(Dashboard.this);
        mUpdateManager.start();
    }

    @Override
    public void onReceiveVersionCode(int code) {

    }

    @Override
    public void onReceiveStalenessDays(int days) {

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
//        Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
        return false;
    }

     void getRecords(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE){

        RequestQueue req = Volley.newRequestQueue(context);
        String url =getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));
                        Log.d(TAG, "onResponse: "+result.length());

                        updateCustomLayout(result.length());

                        //   Snackbar.make(regActivity,result.getJSONObject.get("message"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else if(jsonObject.getString("success").equals("7")){
                        updateCustomLayout(0);
                    }else{

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
//                params.put("DATA",DATA.toString());
                params.put("RESULT_TYPE", REQUEST_TYPE);
                params.put("TABLE", TABLE);
                params.put("WHERE_CLAUSE",WHERE_CLAUSE);
                return params;
            }
        };

        req.add(stringRequest);

    }

    void updateCustomLayout(int count){
        // update custom notification layout
        RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.custom_push);
        contentView.setTextViewText(R.id.title, "Inbox");
        contentView.setTextViewText(R.id.text, ""+count);
        ForegroundService.notBuilder.setCustomContentView(contentView).build();
        synchronized( ForegroundService.manager){
            ForegroundService.manager.notify(1,ForegroundService.notBuilder.build());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // get inbox count for notification
        try {
            itemModel.clear();
            getGav();
            String whereclause = " WHERE status=0 AND username='"+uname+"' ";
            getRecords(this, "GET_RECORDS",  "servicerequest",whereclause);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}