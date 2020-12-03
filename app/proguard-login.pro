
#noinspection ShrinkerUnresolvedReference
-keep class com.kakao.** { *; }

-keepclassmembers class * {
  public static <fields>;
  public *;
}
-dontwarn android.support.v4.**,org.slf4j.**,com.google.android.gms.**


#kakaotalk
-keep class com.kakao.sdk.**.model.* { <fields>; }



#naver
-keep public class com.nhn.android.naverlogin.** {
       public protected *;
}

#noinspection ShrinkerUnresolvedReference
-keep class com.vuforia.engine.wet.* { *; }