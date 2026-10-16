# yt-dlp Android uses reflection and native runtime initialization.
-keep class com.yausername.** { *; }
-keep class com.fasterxml.jackson.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.yausername.**
-dontwarn java.beans.**
-dontwarn org.w3c.dom.**
