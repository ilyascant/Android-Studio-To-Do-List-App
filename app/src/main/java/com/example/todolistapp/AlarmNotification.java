package com.example.todolistapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AlarmNotification extends BroadcastReceiver {

    private static int id = 0;

    public static void setId(int id) {
        AlarmNotification.id = id;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context,HomeActivity.class);
        int sID = intent.getExtras().getInt("ID");
        String sTask = intent.getExtras().getString("TASK");
        String sDescription = intent.getExtras().getString("DESCRIPTION");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,sID,i,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ilyascant");

        builder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(sTask)
                .setContentText(sDescription.substring(0,sDescription.length()>60?60:sDescription.length())+" ...")
                .setContentInfo("Info")
                .setContentIntent(pendingIntent);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
        nmc.notify(1,builder.build());
    }


}