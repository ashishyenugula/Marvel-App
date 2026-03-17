# Marvel App Proguard Rules

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.marvel.app.data.model.** { *; }
-dontwarn retrofit2.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
