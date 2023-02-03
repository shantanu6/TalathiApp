package com.example.talathiapp;

import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent ;
import android.content.Intent ;
import android.os.Bundle ;
import android.view.View ;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity2 extends AppCompatActivity {
    static int count = 0 ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onResume () {
        super .onResume() ;
        count = 0 ;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super .onCreate(savedInstanceState) ;
        setContentView(R.layout. activity_main2 ) ;

//    <uses-permission android:name = "android.permission.VIBRATE" />
//    <uses-permission android:name = "android.permission.RECEIVE_BOOT_COMPLETED" />
    }

    public void createNotification (View view) {
        count ++ ;

        Intent notificationIntent = new Intent(getApplicationContext() , MainActivity. class ) ;
        notificationIntent.putExtra( "fromNotification" , true ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
        PendingIntent pendingIntent = PendingIntent. getActivity ( this, 0 , notificationIntent , 0 ) ;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
        mBuilder.setSmallIcon(R.drawable.applogo);
        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentIntent(pendingIntent) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setAutoCancel( true ) ;
        mBuilder.setBadgeIconType( NotificationCompat.BADGE_ICON_LARGE ) ;
        mBuilder.setNumber( count ) ;

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }
}