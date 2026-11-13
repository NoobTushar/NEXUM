package com.nexum.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/** Tiny window that pops over whatever you were doing — type, save, done. */
public class QuickCaptureActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_quick_capture);
        final EditText in = findViewById(R.id.capIn);

        findViewById(R.id.capSave).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String t = in.getText().toString().trim();
                if (t.isEmpty()) { Toast.makeText(QuickCaptureActivity.this, "Type something first ✍️", Toast.LENGTH_SHORT).show(); return; }
                try {
                    SharedPreferences p = getSharedPreferences("nexum_native", MODE_PRIVATE);
                    JSONArray old = new JSONArray(p.getString("pending_caps", "[]"));
                    JSONObject o = new JSONObject();
                    o.put("type", "text");
                    o.put("t", t);
                    o.put("ts", DateFormat.format("HH:mm", new Date()).toString());
                    JSONArray out = new JSONArray();
                    out.put(o);
                    for (int i = 0; i < old.length(); i++) out.put(old.get(i));
                    p.edit().putString("pending_caps", out.toString()).apply();
                } catch (Exception ignored) {}
                Toast.makeText(QuickCaptureActivity.this, "Captured ✓", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        findViewById(R.id.capOpen).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent in2 = new Intent(QuickCaptureActivity.this, MainActivity.class);
                in2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in2);
                finish();
            }
        });
    }
}
