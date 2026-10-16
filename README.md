# YtDlp Android

تطبيق Android بـ Kotlin وJetpack Compose لتنزيل الصوت والفيديو عبر yt-dlp على الجهاز نفسه.

## ما تم تنفيذه
- فحص رابط الفيديو محلياً عبر `youtubedl-android` وyt-dlp المضمّن.
- قراءة الصيغ والجودات المتاحة من yt-dlp واختيار صيغة فيديو أو صوت.
- تنزيل بالخلفية عبر WorkManager مع retry عند فشل الشبكة.
- قص جزء زمني عبر `--download-sections`، مثال `02:03` إلى `03:56`.
- دمج الفيديو والصوت عبر FFmpeg عند الحاجة.
- حفظ الملفات في `Downloads/YtDlp`.
- سجل Room للحالات: Queued / Downloading / Completed / Failed.
- واجهة Compose بستايل Material 3، Dark Mode، وعربي/إنجليزي واتجاه RTL/LTR.
- GitHub Actions workflow للبناء والفحص وإرفاق APK Debug في كل Push أو Pull Request.

## التشغيل
افتح المشروع في Android Studio Ladybug أو أحدث، ثم شغّل `./gradlew assembleDebug`. يدعم Android 8.0+ ومعماريات `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`.

## Workflow
الملف `.github/workflows/android.yml` ينفذ `lint` و`testDebugUnitTest` و`assembleDebug`، ثم يرفع `app-debug.apk` كـartifact. يمكن تشغيله يدوياً من تبويب Actions عبر `workflow_dispatch`.

## الترخيص والمصدر
المشروع يستخدم [youtubedl-android](https://github.com/yausername/youtubedl-android) الإصدار 0.18.1، وهي مكتبة GPL-3.0 تحتوي yt-dlp وPython وFFmpeg مهيأة لـAndroid. راجع شروط YouTube وحقوق المحتوى قبل الاستخدام، وحدّث binary من داخل المكتبة عند الحاجة.
