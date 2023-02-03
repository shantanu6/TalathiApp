package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
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

import java.nio.channels.GatheringByteChannel;
import java.util.HashMap;
import java.util.Map;

public class GavDashboard extends AppCompatActivity implements View.OnClickListener {

    static String GCODE,GNAME;
    ConstraintLayout GavDashboard;
    TextView txtGavName,txtInboxCount,txtOutboxCount;
    String TAG="GavDash",uname;
    LinearLayout inboxCardView,outboxCardView,reportCardView,cardtutorial,cardImpgr,cardProfile;
    private String inbCount,outCount;
    SharedPreferences user_details;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gav_dashboard);
        getSupportActionBar().setTitle("Gav Dashboard");
        GavDashboard=findViewById(R.id.GavDashboard);
        txtGavName=findViewById(R.id.txtGavName);
        txtInboxCount=findViewById(R.id.txtInboxCount);
        txtOutboxCount=findViewById(R.id.txtOutboxCount);
        inboxCardView=findViewById(R.id.inboxCardView);
        outboxCardView=findViewById(R.id.outboxCardView);
        reportCardView=findViewById(R.id.reportCardView);
        cardtutorial=findViewById(R.id.cardtutorial);
        cardImpgr=findViewById(R.id.cardImpgr);
        cardProfile=findViewById(R.id.cardProfile);

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);

        pd =new ProgressDialog(this);
        pd.setTitle("Fetching Details");
        pd.setMessage("Please Wait");
        pd.setCancelable(false);

        user_details = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        uname = user_details.getString("UNAME", "");

        GCODE=getIntent().getStringExtra("GCODE");
        GNAME=getIntent().getStringExtra("GNAME");
        txtGavName.setText(GNAME);

        RequestQueue req = Volley.newRequestQueue(this);
        String url =getString(R.string.db_url);
        getDetails(req,url);

        inboxCardView.setOnClickListener(this);
        outboxCardView.setOnClickListener(this);
        reportCardView.setOnClickListener(this);
        cardtutorial.setOnClickListener(this);
        cardImpgr.setOnClickListener(this);
        cardProfile.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inboxCardView:
                Intent i1=new Intent(GavDashboard.this,Inbox.class);
                i1.putExtra("GCODE",GCODE);
                i1.putExtra("GNAME",GNAME);

                if(!inbCount.equals("0")) {
                    startActivity(i1);
                }
                else
                    Snackbar.make(GavDashboard,"Inbox Empty", BaseTransientBottomBar.LENGTH_SHORT ).show();
                break;
            case R.id.outboxCardView:
                Intent i2=new Intent(GavDashboard.this,Outbox.class);
                i2.putExtra("GCODE",GCODE);
                i2.putExtra("GNAME",GNAME);

                if(!outCount.equals("0"))
                    startActivity(i2);
                else
                    Snackbar.make(GavDashboard,"Outbox Empty", BaseTransientBottomBar.LENGTH_SHORT ).show();
                break;
            case R.id.reportCardView:
                Intent i3=new Intent(GavDashboard.this,Report.class);
                i3.putExtra("GCODE",GCODE);
                i3.putExtra("GNAME",GNAME);
                startActivity(i3);
                break;
            case R.id.cardtutorial:
                Intent i4=new Intent(GavDashboard.this,WebViewAct.class);
                i4.putExtra("url",getString(R.string.tutorial_url));
                i4.putExtra("act_name","Tutorial");
                startActivity(i4);
                break;
            case R.id.cardImpgr:
                Intent i5=new Intent(GavDashboard.this,WebViewAct.class);
                i5.putExtra("url",getString(R.string.imp_gr_url));
                i5.putExtra("act_name","Important GR");
                startActivity(i5);
                break;
            case R.id.cardProfile:
                    startActivity(new Intent(GavDashboard.this,UserProfile.class));
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "Restart", Toast.LENGTH_SHORT).show();
        RequestQueue req = Volley.newRequestQueue(this);
        String url =getString(R.string.db_url);
        getDetails(req,url);

        // get inbox count for notification
        try {
            String whereclause = " WHERE status=0 AND username='"+uname+"' ";
          getRecords(this, "GET_RECORDS",  "servicerequest",whereclause);
        }catch (Exception e){
            e.printStackTrace();
        }
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


    void getDetails(RequestQueue req, String url){

        pd.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("SUCCESS").equals("1")){
                        JSONObject result = new JSONObject(jsonObject.getString("RESULT"));

//                        Log.d(TAG, "onResponse: "+result.getString("GAV"));

                        txtInboxCount.setText(result.getString("INBOX"));
                        txtOutboxCount.setText(result.getString("OUTBOX"));

                        inbCount=result.getString("INBOX");
                        outCount=result.getString("OUTBOX");

                        Snackbar.make(GavDashboard,jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(GavDashboard, jsonObject.getString("MESSAGE"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap<String,String>();
                params.put("RESULT_TYPE","GET_SPECIFIC_GAV");
                params.put("GCODE",GCODE);
                return params;
            }
        };

        req.add(stringRequest);
    }


}