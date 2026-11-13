# Keep the JS bridge methods (called from JavaScript)
-keepclassmembers class com.nexum.app.MainActivity$Bridge {
    @android.webkit.JavascriptInterface <methods>;
}
