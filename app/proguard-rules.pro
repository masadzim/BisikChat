# ProGuard configuration
-keep public class * {
    public protected *;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.Application

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
