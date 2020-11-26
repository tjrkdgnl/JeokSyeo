# Begin: Proguard rules for Firebase

# Authentication
-keepattributes *Annotation*

# Realtime database
-keepattributes Signature

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.*

# End: Proguard rules for Firebase