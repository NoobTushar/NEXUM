package com.nexum.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/** Fires a real Android notification at reminder time, then re-arms itself for tomorrow. */
public class ReminderReceiver extends BroadcastReceiver {

    public static final String CH = "nexum_rems";

    @Override
    public void onReceive(Context c, Intent in) {
        String text = in.getStringExtra("t");
        String tm = in.getStringExtra("tm");
        int id = in.getIntExtra("id", 0);

        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel ch = new NotificationChannel(CH, "Reminders", NotificationManager.IMPORTANCE_HIGH);
        ch.enableVibration(true);
        nm.createNotificationChannel(ch);

        Intent open = new Intent(c, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, id, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        nm.notify(id, new NotificationCompat.Builder(c, CH)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle("NEXUM ⏰")
                .setContentText(text == null ? "Time to work!" : text)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build());

        // daily repeat
        if (tm != null) ReminderScheduler.scheduleNext(c, id, text, tm);
    }
}
