package com.example.todolist.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todolist.MainActivity;
import com.example.todolist.R;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationID = intent.getExtras().getInt("id");

        String info = "Reminder of your task from category: " + intent.getStringExtra("CATEGORY") + " at. " + intent.getStringExtra("TIME") + ", " + intent.getStringExtra("DATE");
        receiveNotification(notificationID, context, intent, intent.getStringExtra("TITLE"), info);
    }

    public void receiveNotification(int id, Context context, Intent intent, String title, String info){
        intent = new Intent(context, MainActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationChannel");
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentText(info);
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }
}
