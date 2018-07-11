# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/duqian/Library/Android/sdk/tools/proguard/proguard-android.txt
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


-dontusemixedcaseclassnames          #混淆时不使用大小写混合类名
-dontskipnonpubliclibraryclasses     #不跳过library中的非public的类
-verbose                             #打印混淆的详细信息
-dontoptimize                        #不进行优化，建议使用此选项，
-dontpreverify                       #不进行预校验,Android不需要,可加快混淆速度。
-ignorewarnings                      #忽略警告
#-optimizationpasses 5               #指定代码的压缩级别

#接入Google原生的一些服务时使用的
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService


-keepattributes *Annotation* #注解
# For native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
# keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
#XML中调用activity方法,混淆了会找不到方法，如点击事件
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
   public void *(android.view.View);
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#枚举类型不混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
#R文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

-dontwarn android.support.**  #表示对android.support包下的代码不警告
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

-keepattributes Signature #范型
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }


#duqian new---------------------------------------
#Gson混淆配置
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.***
-keep class com.google.gson.** { *; }

#保持以下不混淆 oair相关
-keep class site.duqian.autowechat.android.view.** { *; }

-keepclassmembers public class site.duqian.ssy.model.entities.** {
   void set*(***);
   *** get*();
}
#-keep class site.duqian.oair_cloud.JavaScriptInterface { *; }#webview js


#butterknife不混淆
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-keep interface okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
-keep interface okio.**{*;}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn rx.**
-keep class rx.**{*;}

-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**

-dontwarn de.robv.android.xposed.**
-keep class de.robv.android.xposed.**{*;}

-dontwarn com.easy.wtool.sdk.**
-keep class com.easy.wtool.sdk.**{*;}

-keep class site.duqian.autowechat.xposed.**{*;}


