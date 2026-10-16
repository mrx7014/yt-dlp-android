# YtDlp Android

تطبيق Android حديث بـ Kotlin وJetpack Compose لتجربة تنزيل الفيديوهات والصوت عبر محرك yt-dlp.

## المميزات
- اختيار الصيغة والجودة للصوت أو الفيديو.
- تحديد جزء من الفيديو عبر وقت البداية والنهاية.
- Material 3 Expressive-inspired UI متجاوب مع الوضع الداكن.
- العربية والإنجليزية مع اتجاه RTL/LTR.
- سجل محلي للتنزيلات وحالتها وخصائصها.
- بنية قابلة لاستبدال محرك yt-dlp بواجهة Native/Termux/Backend.

## تشغيل المشروع
1. افتح المشروع في Android Studio Ladybug أو أحدث.
2. دع Gradle ينزّل الاعتمادات.
3. شغّل التطبيق على جهاز Android 8+.

> ملاحظة: طبقة `YtDlpEngine` هي نقطة التكامل مع binary الخاص بـ yt-dlp. في هذه النسخة يوجد `MockYtDlpEngine` للعرض والتطوير، مع أمر واضح في الكود لاستبداله بمشغل حقيقي. لا تضع مفاتيح أو binaries كبيرة داخل Git.

## تكامل yt-dlp الحقيقي
ضع binary مناسباً للمعمارية في `filesDir/bin/yt-dlp` أو اربط `ProcessYtDlpEngine` بمصدر binary موثوق، ثم مرّر نتائج `--dump-single-json` إلى `VideoInfo` ونفّذ التنزيل عبر WorkManager. راجع شروط YouTube وحقوق المحتوى قبل الاستخدام.

## البنية
- `data`: Room entities وrepository.
- `domain`: نماذج ومحرك yt-dlp.
- `ui`: Compose screens وViewModel.
