package com.nexum.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Turns the reminders you set inside NEXUM into REAL Android alarms,
 * so they fire even when the app is closed.
 */
public class ReminderScheduler {

    public static void setAll(Context c, String json) {
        SharedPreferences p = c.getSharedPreferences("nexum_native", Context.MODE_PRIVATE);
        p.edit().putString("rems", json).apply();
        rebuild(c);
    }

    /** Cancel everything previously scheduled, then schedule every active reminder daily. */
    public static void rebuild(Context c) {
        SharedPreferences p = c.getSharedPreferences("nexum_native", Context.MODE_PRIVATE);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        // cancel old alarms
        try {
            JSONArray old = new JSONArray(p.getString("alarm_ids", "[]"));
            for (int i = 0; i < old.length(); i++) {
                int id = old.getInt(i);
                PendingIntent pi = PendingIntent.getBroadcast(c, id,
                        new Intent(c, ReminderReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                am.cancel(pi);
            }
        } catch (Exception ignored) {}

        JSONArray ids = new JSONArray();
        try {
            JSONArray arr = new JSONArray(p.getString("rems", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject r = arr.getJSONObject(i);
                if (!r.optBoolean("on", true)) continue;
                String tm = r.optString("tm", "");
                if (tm.length() != 5) continue;
                int id = (int) (Math.abs(r.optLong("id", i)) % 1000000000L);
                ids.put(id);
                scheduleNext(c, id, r.optString("t", "Reminder"), tm);
            }
            p.edit().putString("alarm_ids", ids.toString()).apply();
        } catch (Exception e) {
            Log.w("NEXUM", "reminder scheduling failed", e);
        }
    }

    /** Schedule one reminder at its next hh:mm occurrence (today if still ahead, else tomorrow). */
    public static void scheduleNext(Context c, int id, String text, String tm) {
        try {
            String[] hh = tm.split(":");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh[0].trim()));
            cal.set(Calendar.MINUTE, Integer.parseInt(hh[1].trim()));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_YEAR, 1);

            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            Intent in = new Intent(c, ReminderReceiver.class)
                    .putExtra("t", text)
                    .putExtra("tm", tm)
                    .putExtra("id", id);
            PendingIntent pi = PendingIntent.getBroadcast(c, id, in,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } catch (Exception ignored) {}
    }
}
