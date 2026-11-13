package com.nexum.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/** A small gold Capture bubble that floats over EVERY app on the phone. */
public class FloatingBubbleService extends Service {

    private WindowManager wm;
    private View bubble;
    private float downX, downY, startX, startY;
    private boolean dragged;

    @Override
    public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent i, int f, int s) {
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return START_NOT_STICKY; }
        if (bubble != null) return START_STICKY;

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        float d = getResources().getDisplayMetrics().density;
        int size = (int) (54 * d);

        TextView v = new TextView(this);
        v.setText("\uD83D\uDCF8");           // 📸
        v.setTextSize(22);
        v.setGravity(android.view.Gravity.CENTER);
        v.setBackgroundResource(R.drawable.bubble_bg);

        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                size, size, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = size / 2;
        lp.y = (int) (220 * d);

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = e.getRawX(); downY = e.getRawY();
                        startX = lp.x; startY = lp.y;
                        dragged = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = e.getRawX() - downX, dy = e.getRawY() - downY;
                        if (Math.abs(dx) > 8 || Math.abs(dy) > 8) dragged = true;
                        lp.x = (int) (startX + dx);
                        lp.y = (int) (startY + dy);
                        try { wm.updateViewLayout(view, lp); } catch (Exception ignored) {}
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!dragged) openCapture();
                        return true;
                }
                return false;
            }
        });

        bubble = v;
        wm.addView(v, lp);
        return START_STICKY;
    }

    private void openCapture() {
        Intent in = new Intent(this, QuickCaptureActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
    }

    @Override
    public void onDestroy() {
        if (bubble != null) {
            try { wm.removeView(bubble); } catch (Exception ignored) {}
            bubble = null;
        }
        super.onDestroy();
    }
}
