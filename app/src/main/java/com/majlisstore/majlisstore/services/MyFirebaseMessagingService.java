package com.majlisstore.majlisstore.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.majlisstore.majlisstore.R;
import com.majlisstore.majlisstore.activities.Dashboard;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String FCM="",message,path="";
    String title ;

    public MyFirebaseMessagingService()
    {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0)
        {
            try
            {

                FCM = remoteMessage.getData().get("FCM");
                message = remoteMessage.getData().get("body");
                title = remoteMessage.getData().get("title");
                path = remoteMessage.getData().get("path");
                if(FCM.equals("Success"))
                {
                    sendNotification(message,title);

                }

            }
            catch (Exception ignored)
            {

            }
        }

    }


    private void sendNotification(String message, String title)
    {
        try {
            Intent intent;
            intent = new Intent(this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this, "Notification")
                    .setSmallIcon(R.drawable.logo_icon)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(new long[]{1000})
                    .setGroupSummary(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "Notification";
                NotificationChannel channel = new NotificationChannel(channelId,
                        title,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelId);
            }


            notificationManager.notify(0, notificationBuilder.build());
        }
        catch (Exception ignored)
        {

        }
    }
}
