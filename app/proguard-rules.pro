# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# 以上是必须的↓↓↓↓↓↓↓
# -----------------------------------------------------------------------------
-verbose
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses

-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontoptimize
-ignorewarnings

-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod,JavascriptInterface

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements java.io.Serializable {
  public static final android.os.Parcelable$Creator *;
}

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**

-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.database.sqlite.SQLiteOpenHelper{ *; }

# 以上是必须的↑↑↑↑↑↑↑
# -----------------------------------------------------------------------------

# 以下是高德地图相关↓↓↓↓↓↓↓
# -----------------------------------------------------------------------------
# 3D 地图
-keep   class com.amap.api.mapcore.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
# 定位
-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}
# 搜索
-keep   class com.amap.api.services.**{*;}
# 2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
# 以上是高德地图相关↑↑↑↑↑↑↑
# -----------------------------------------------------------------------------

# 以下是本程序相关↓↓↓↓↓↓↓
# -----------------------------------------------------------------------------
-keep  public class cn.common.http.JsonParse
-keep  public class * extends  cn.common.http.base.BaseResponse
-keep  public class * extends  cn.common.ui.BaseDialog
-keep  public class cn.protector.logic.entity.**{*;}
-keep  public class cn.protector.logic.http.response.**{*;}
# 以上是本程序相关↑↑↑↑↑↑↑
# -----------------------------------------------------------------------------

