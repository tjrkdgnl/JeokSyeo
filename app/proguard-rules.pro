# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##-dontwarn 패키지명.** : 지정해서 경고 무시
##-dontwarn 패키지명.** : 난독화가 필요하지 않은 경우
##-ignorewarnings : 경고 무시
##-dontoptimize : 최적화 하지 않기
##-dontshrink : 사용하지 않는 메소드 유지
##-keepclassmembers : 특정 클래스 멤버 원상태 유지
## -keepattributes : 내부 클래스 원상태 유지 적용


#버전 업그레이드 후, 프로가드 에러 제거
#noinspection ShrinkerUnresolvedReference


-keep public class * extends java.lang.Exception

-keepattributes SourceFile,LineNumberTable ##소스파일, 라인 정보 유지
-keepattributes Signature
-keepnames class org.apache.http.* { *; }

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.* {
    volatile <fields>;
}


#-assumenosideeffects class android.util.Log { *; } //로그 모두 막아버리면 api 통신 안됨
-assumenosideeffects class android.util.Log {

    public static int v (...);

    public static int d (...);

    public static int i (...);

    public static int w (...);

    public static int e (...);

    public static int wtf (...);
}
