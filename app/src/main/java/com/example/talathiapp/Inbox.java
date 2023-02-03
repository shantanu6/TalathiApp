package com.example.talathiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Inbox extends AppCompatActivity {

    RecyclerView inboxRecylerView;
    ArrayList<InboxItem> itemModel=new ArrayList<>();
    String TAG="Inbox";
    static String GCODE,GNAME;
    InboxRecyclerView inboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        inboxRecylerView=findViewById(R.id.inboxRecylerView);
        inboxRecylerView.setLayoutManager(new LinearLayoutManager(this));

        // Check Is Valid Token Exists
        CheckToken ct=new CheckToken();
        ct.isExitToken(this);


        GCODE=getIntent().getStringExtra("GCODE");
        GNAME=getIntent().getStringExtra("GNAME");
        getSupportActionBar().setTitle("Inbox- "+GNAME);

        RequestQueue req = Volley.newRequestQueue(this);
        String url =getString(R.string.db_url);
        get_inboxDetails(req,url);

    }

    void get_inboxDetails(RequestQueue req, String url){
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
                            InboxItem ii=new InboxItem();
                            ii.setInGavID(result.getJSONObject(i).getString("GID"));
                            ii.setInDate(result.getJSONObject(i).getString("DATE"));
                            ii.setInCertName(result.getJSONObject(i).getString("CERT_NAME"));
                            ii.setInUname(result.getJSONObject(i).getString("UNAME"));
                            ii.setInGcode(result.getJSONObject(i).getString("GCODE"));
                            ii.setInAppId(result.getJSONObject(i).getString("APP_ID"));
                            ii.setIncome("Income ₹."+result.getJSONObject(i).getString("INCOME"));
                            ii.setInServiceNo("Service No :"+result.getJSONObject(i).getString("SERVICE_NO"));
                            itemModel.add(ii);
                        }
                        inboxAdapter = new InboxRecyclerView(Inbox.this, itemModel,inboxRecylerView);
                        inboxRecylerView.setAdapter(inboxAdapter);

                        Snackbar.make(inboxRecylerView,jsonObject.getString("MESSAGE"), BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.green)).show();
                    }else {
                        Snackbar.make(inboxRecylerView, jsonObject.getString("MESSAGE"),BaseTransientBottomBar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.red)).show();
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
                params.put("RESULT_TYPE","GET_INBOXES");
                params.put("GCODE",GCODE);
                return params;
            }
        };

        req.add(stringRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "Restart", Toast.LENGTH_SHORT).show();
    }

}