#noinspection ShrinkerUnresolvedReference
#kakaotalk
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

#naver
-keep public class com.nhn.android.naverlogin.** {
       public protected *;
}
