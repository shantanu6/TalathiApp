package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PDFPrint;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Outbox extends AppCompatActivity {

    RecyclerView outboxRecylerView;
    ArrayList<OutboxItem> itemModel=new ArrayList<>();
    String TAG="Outbox";
    static String GCODE,GNAME;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbox);
        outboxRecylerView=findViewById(R.id.outboxRecylerView);
        outboxRecylerView.setLayoutManager(new LinearLayoutManager(this));

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);


        GCODE=getIntent().getStringExtra("GCODE");
        GNAME=getIntent().getStringExtra("GNAME");
        getSupportActionBar().setTitle("Outbox - "+GNAME);

        RequestQueue req = Volley.newRequestQueue(this);
        String url =getString(R.string.db_url);
        get_outboxDetails(req,url);
    }

    void get_outboxDetails(RequestQueue req, String url){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("SUCCESS").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("RESULT"));

                        for(int i=0;i<result.length();i++){
                            Log.d(TAG, "onResponse: "+result.getJSONObject(i));
                            OutboxItem oi=new OutboxItem();
                            oi.setOutGavID(result.getJSONObject(i).getString("GID"));
                            oi.setOutDate(result.getJSONObject(i).getString("DATE"));
                            oi.setOutCertName(result.getJSONObject(i).getString("CERT_NAME"));
                            oi.setOutUname(result.getJSONObject(i).getString("UNAME"));
                            oi.setOutMobile(result.getJSONObject(i).getString("MOBILE"));
                            oi.setOutGcode(result.getJSONObject(i).getString("GCODE"));
                            oi.setOutAppId(result.getJSONObject(i).getString("APP_ID"));
                            oi.setOutServiceNo("Service No :"+result.getJSONObject(i).getString("SERVICE_NO"));

                            if(result.getJSONObject(i).getString("STATUS").equals("1")){
                                oi.setOutStatus("Accepted");
                            }
                            else  if(result.getJSONObject(i).getString("STATUS").equals("2")){
                                oi.setOutStatus("Rejected");
                            }
                            else{
                                oi.setOutStatus("-");
                            }

                            itemModel.add(oi);
                        }
                        OutboxRecyclerView outboxAdapter = new OutboxRecyclerView(Outbox.this, itemModel,outboxRecylerView);
                        outboxRecylerView.setAdapter(outboxAdapter);

                        Snackbar.make(outboxRecylerView,jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(outboxRecylerView, jsonObject.getString("MESSAGE"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
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
                params.put("RESULT_TYPE","GET_OUTBOXES");
                params.put("GCODE",GCODE);
                return params;
            }
        };

        req.add(stringRequest);
    }

}