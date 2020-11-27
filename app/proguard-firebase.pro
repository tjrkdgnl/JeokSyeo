# Begin: Proguard rules for Firebase

# Authentication
-keepattributes *Annotation*

# Realtime database
-keepattributes Signature

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.*


# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes SourceFile,LineNumberTable,*Annotation*
-keep class com.crashlytics.android.**

# End: Proguard rules for Firebase