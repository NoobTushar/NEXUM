package com.nexum.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** After phone restart, re-arms all reminders automatically. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
            ReminderScheduler.rebuild(c);
        }
    }
}
