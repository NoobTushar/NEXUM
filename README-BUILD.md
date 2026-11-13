# 📱 Build NEXUM as a REAL Android app — step by step

Your NEXUM app becomes a true installed app with its own icon on your phone:
full-screen, real notifications that fire even when the app is closed, a gold
Capture bubble floating over every app, and links that open the real YouTube /
Instagram apps.

**Works on Android 8.0 or newer.**

---

## Step 1 — Install Android Studio (one time, free)
1. Go to **developer.android.com/studio**
2. Download and install it (just keep pressing Next; ~2 GB)

## Step 2 — Open the project
1. Open Android Studio
2. Click **Open** and choose the folder **`NEXUM-ANDROID`** (this folder)
3. If it asks "Trust project?" → **Trust**
4. Wait for it to finish "syncing" (needs internet, 5–10 minutes the first time)

## Step 3 — Make the app file (APK)
1. Menu **Build → Build App Bundle(s) / APK(s) → Build APK(s)**
2. When finished, click **locate** in the popup (or find it at
   `app/build/outputs/apk/debug/app-debug.apk`)

## Step 4 — Put it on your phone
1. Copy **app-debug.apk** to your phone (USB, WhatsApp to yourself, Google Drive…)
2. On the phone, tap the file. If it warns about unknown apps, tap
   **Settings → allow from this source**, then **Install**
3. **NEXUM** appears in your app list with the gold icon — just like every other app 🎉

## Step 5 — Turn on the two superpowers
1. **Floating Capture bubble:** when you first open NEXUM, Android asks
   **"Display over other apps"** → tap NEXUM → allow.
   A gold 📸 bubble now floats over *every* app. Tap it anywhere → quick-capture
   window pops up → type → **Save**. It appears in NEXUM → Notes → Captures.
   (Drag the bubble to move it.)
2. **Reminders:** first time Android asks about notifications → **Allow**.
   Also: Settings → Apps → NEXUM → Alarms & reminders → **allow**.
   Now reminders you set inside NEXUM ring **even when the app is closed**,
   and come back automatically after you restart the phone.

## What works automatically
- ✅ Your whole app (plan, habits, growth videos, focus, music, research…)
- ✅ Data saves on your phone and stays after closing
- ✅ Tapping a YouTube/Instagram/X link opens the **real app**, not a browser page
- ✅ Reminders fire as real notifications (closed app, even after reboot)
- ✅ Floating capture bubble over any app
- ✅ Music starts instantly when you enter

## Honest limits (v1)
- Screenshots from the bubble need Android's built-in screenshot first
  (power + volume-down), then NEXUM → Capture → Choose image.
  Full one-tap screen capture needs a special Google permission — a later update.
- Reminders repeat daily at the set time.

## Publish on Google Play (later, optional)
Menu **Build → Generate Signed Bundle / APK → Android App Bundle**, create a
keystore (follow the wizard), then upload at **play.google.com/console**
($25 one-time developer fee). I can walk you through it when you're ready.
