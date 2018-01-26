#---------------------------------------------------------------------------------------------------
-renamesourcefileattribute SourceFile
-keepparameternames #【是否保留方法内参数命名】外壳暂时不加,影响开发者理解,但可以减小约1KB
-optimizationpasses 5 #【优化轮数】,外壳不需要加,增加包大小
#-dontusemixedcaseclassnames #【混淆时不会产生形形色色的类名】,不会影响包大小
-dontskipnonpubliclibraryclasses #【指定不去忽略非公共的库类】,不会影响包大小
-dontskipnonpubliclibraryclassmembers
-dontpreverify #【不预校验】,需要打开,减少包大小,android不需预校验
-dontoptimize
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* #【优化】,外壳可不加,加上包大小略微变大
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-verbose
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod,*Annotation*
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
#自定义组件不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#保持注解继承类不混淆
-keep class * extends java.lang.annotation.Annotation {*;}
#-------------------------------------------定制化区域----------------------------------------------

-dontwarn kotlin.**
-dontwarn android.**
-dontwarn org.**
-dontwarn com.github.**
-dontwarn com.google.**
-keep class kotlin.**{*;}
-keep class android.**{*;}
-keep class org.**{*;}
-keep class com.github.**{*;}
-keep class com.google.**{*;}

#---------------------------------1.实体类---------------------------------



#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------

-keep class com.alibaba.**{*;}
-dontwarn com.alibaba.**

#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------