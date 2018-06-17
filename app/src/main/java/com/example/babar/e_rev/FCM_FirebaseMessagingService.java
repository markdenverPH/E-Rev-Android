package com.example.babar.e_rev;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCM_FirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sp;
        sp = getSharedPreferences("IDValue", 0);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FCM_from", "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
        //works with fore/background
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM_payload", "Message data payload: " + remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();
            String title = params.get("title");
            String body = params.get("body");
            Log.d("FCM_BODY", "Message Notification Body: " + body);
            Log.d("FCM_TITLE", "Message Notification Title: " + title);
            pop_notif(body, title);
        }

    }

    public void pop_notif(String body, String title){
        SharedPreferences sp = getSharedPreferences("NotifyID", 0);
        int notify_id = sp.getInt("notify_id", 0);
        Log.d("NotifyID_get", String.valueOf(notify_id));

        //set intent to open app
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notif_builder = new NotificationCompat.Builder(this, "E-Rev")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_final2))
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND|NotificationCompat.DEFAULT_LIGHTS| NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notify_id, notif_builder.build());
        //update notify_id to sp
        SharedPreferences.Editor spedit = sp.edit();
        spedit.putInt("notify_id", notify_id+1);
        spedit.apply();
        Log.d("NotifyID_update", String.valueOf(sp.getInt("notify_id", 0)));
    }

}
