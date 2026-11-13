package com.nexum.app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        web = findViewById(R.id.web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);   // music/chime autoplay works
        web.setBackgroundColor(0xFF0A0A0A);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                if (url.startsWith("file:")) return false;      // our own app
                // everything else (YouTube, Instagram, X, Reddit...) opens the REAL app
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception ignored) {}
                return true;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                loaded = true;
                injectNative();
            }
        });
        web.setWebChromeClient(new WebChromeClient());          // enables alert/confirm from the app
        web.addJavascriptInterface(new Bridge(), "Native");
        web.loadUrl("file:///android_asset/index.html");

        // notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        // floating capture bubble (needs "Display over other apps")
        if (Settings.canDrawOverlays(this)) {
            startService(new Intent(this, FloatingBubbleService.class));
        } else {
            Toast.makeText(this, "Tip: allow \u201cDisplay over other apps\u201d for NEXUM to get the floating Capture button", Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            } catch (Exception ignored) {}
        }
    }

    /** Push pending captures into the app + hand reminders to the native alarm system. */
    private void injectNative() {
        if (web == null) return;
        String js =
            "(function(){try{" +
              "var p=Native.pendingCaptures();" +
              "if(p&&p.length>2){" +
                "var caps=JSON.parse(localStorage.getItem('nexum_captures')||'[]');" +
                "JSON.parse(p).forEach(function(c){caps.unshift(c)});" +
                "localStorage.setItem('nexum_captures',JSON.stringify(caps));" +
                "Native.clearPending();location.reload();return;" +
              "}" +
              "Native.setReminders(localStorage.getItem('nexum_rems')||'[]');" +
            "}catch(e){}})()";
        web.evaluateJavascript(js, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loaded) injectNative();   // pick up captures saved from the floating bubble
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }

    /** JavaScript bridge used by the app inside the WebView. */
    class Bridge {
        @JavascriptInterface
        public void setReminders(String json) {
            ReminderScheduler.setAll(getApplicationContext(), json);
        }

        @JavascriptInterface
        public String pendingCaptures() {
            return getSharedPreferences("nexum_native", MODE_PRIVATE)
                    .getString("pending_caps", "[]");
        }

        @JavascriptInterface
        public void clearPending() {
            getSharedPreferences("nexum_native", MODE_PRIVATE).edit().remove("pending_caps").apply();
        }

        @JavascriptInterface
        public void toast(final String m) {
            runOnUiThread(new Runnable() {
                @Override public void run() { Toast.makeText(MainActivity.this, m, Toast.LENGTH_SHORT).show(); }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] p, @NonNull int[] g) {
        super.onRequestPermissionsResult(code, p, g);
    }
}
