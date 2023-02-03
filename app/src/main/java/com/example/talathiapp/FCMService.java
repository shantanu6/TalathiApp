package com.example.talathiapp;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.app.Notification.PRIORITY_DEFAULT;
import static android.app.Notification.VISIBILITY_PUBLIC;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class FCMService extends FirebaseMessagingService {

    String TAG="FCM",uname;
    Intent intent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "onMessageReceived: "+message.getData());

        SharedPreferences user_details = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        uname = user_details.getString("UNAME", "");

        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_id);
            String description = "Firebase Messages";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("2", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("body"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(PRIORITY_DEFAULT); //Important for heads-up notification

        try {
            if(message.getData().get("large_icon")!=null){
                InputStream in = new URL(message.getData().get("large_icon")).openStream();
                Bitmap bmpLargeIcon = BitmapFactory.decodeStream(in);
                mBuilder.setLargeIcon(bmpLargeIcon);
            }
            if(message.getData().get("image")!=null){
                InputStream in1 = new URL(message.getData().get("image")).openStream();
                Bitmap bmpImage = BitmapFactory.decodeStream(in1);
                 mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmpImage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(2, buildNotification);

        if(message.getData().get("request").equals("Inbox")){
            try {
                String whereclause = " WHERE status=0 AND username='"+uname+"' ";
                getRecords(this, "GET_RECORDS",  "servicerequest",whereclause);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setupNotificationChannel() {
        String name = getString(R.string.channel_id);
        int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
        NotificationChannel channel = new NotificationChannel("2", name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

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

}
