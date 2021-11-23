# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\LENOVO\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-dontwarn org.apache.http.**
-dontwarn android.net.**
-keep class org.apache.** {*;}
-keep class org.apache.http.** { *; }
-keepclassmembers enum * { *; }

-keep,includedescriptorclasses class org.bouncycastle.** { *; }
-dontwarn javax.naming.**
-keep class com.google.** {*;}
-dontnote sun.java2d.cmm.kcms.KcmsServiceProvider
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
#  -keep class net.sourceforge.zbar.** { *; }
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
