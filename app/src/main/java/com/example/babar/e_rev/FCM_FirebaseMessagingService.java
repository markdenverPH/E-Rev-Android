package com.example.babar.e_rev;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCM_FirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FCM_from", "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d("FMS_payload", "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            String data = remoteMessage.getData().get("test");
            Log.d("FCM_DATA", data);
            Log.d("FCM_BODY", "Message Notification Body: " + body);
            Log.d("FCM_TITLE", "Message Notification Title: " + title);
            pop_notif(body, title);
        }
    }

    public void pop_notif(String body, String title){
        SharedPreferences sp = getSharedPreferences("NotifyID", 0);
        int notify_id = sp.getInt("notify_id", 0);
        Log.d("NotifyID_get", String.valueOf(notify_id));

        NotificationCompat.Builder notif_builder = new NotificationCompat.Builder(this, "E-Rev")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_final2))
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(NotificationCompat.DEFAULT_SOUND|NotificationCompat.DEFAULT_LIGHTS| NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notify_id, notif_builder.build());
        //update notify_id to sp
        SharedPreferences.Editor spedit = sp.edit();
        spedit.putInt("notify_id", notify_id+1);
        spedit.apply();
        Log.d("NotifyID_update", String.valueOf(notify_id+1));
    }

}
