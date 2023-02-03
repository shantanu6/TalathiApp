package com.example.talathiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;

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

import java.util.HashMap;
import java.util.Map;

public class CheckToken {

    static String deviceId;
    String TAG="Token";
    String UID;
    Context context;

//    my token 803543cf66ce571b

    public void isExitToken(Context context){
    this.context=context;

        UID=context.getSharedPreferences("USER_DETAILS",context.MODE_PRIVATE).getString("UID","");
        deviceId = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        try {
            String whereclause = " WHERE id='"+UID+"' AND token='"+deviceId+"' ";
            getDbToken(context, "GET_RECORDS",  "users",whereclause,"GET_TOKEN");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void getDbToken(Context context, String REQUEST_TYPE, String TABLE, String WHERE_CLAUSE, String RESULT_TYPE){

        RequestQueue req = Volley.newRequestQueue(context);
        String url =context.getString(R.string.db_url);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                Log.d(TAG, "onResponse: "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("success").equals("1")){
                        JSONArray result = new JSONArray(jsonObject.getString("result"));
                        Log.d(TAG, "onResponse: "+result);
//                        Toast.makeText(context, "Valid Token", Toast.LENGTH_SHORT).show();

                    }else if(jsonObject.getString("success").equals("7")) {
                        updateCustomLayout(0);
                        // Remove Sticky notification
                        Intent stopIntent = new Intent(context, ForegroundService.class);
                        context.stopService(stopIntent);

                        Toast.makeText(context, "Unauthorized Device", Toast.LENGTH_LONG).show();
                        // Logout :- Device Token Invalid / Change
                        context.getSharedPreferences("USER_DETAILS",context.MODE_PRIVATE).edit().clear().commit();
                        context.startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                        ((Activity)context).finish();
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

    void updateCustomLayout(int count){
        // update custom notification layout
        RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.custom_push);
        contentView.setTextViewText(R.id.title, "Inbox");
        contentView.setTextViewText(R.id.text, ""+count);
        ForegroundService.notBuilder.setCustomContentView(contentView).build();
        synchronized( ForegroundService.manager){
            ForegroundService.manager.notify(1,ForegroundService.notBuilder.build());
        }
    }


}
