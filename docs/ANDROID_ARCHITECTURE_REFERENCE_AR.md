# تفاصيل هيكلة مجلدات المشروع في تطبيق Android (فكرة فيديو)

تعتمد الهيكلية على **Feature-based + Layered Architecture**: تقسيم المشروع أولًا حسب الميزات، وداخل كل ميزة فصل `UI` و`ViewModel` عن البيانات والمنطق. هذا يتوافق مع إرشادات Android التي تعتمد طبقة UI وطبقة Data، مع Domain اختيارية عند وجود منطق معقد أو مشترك.

## الهيكل المقترح

```text
android-app/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/
│       │   │   ├── FikraVideoApp.kt
│       │   │   ├── MainActivity.kt
│       │   │   │
│       │   │   ├── core/
│       │   │   ├── data/
│       │   │   ├── domain/
│       │   │   ├── feature/
│       │   │   └── navigation/
│       │   │
│       │   └── res/
│       │       ├── drawable/
│       │       ├── mipmap/
│       │       ├── values/
│       │       └── xml/
│       │
│       ├── test/
│       └── androidTest/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── gradle/libs.versions.toml
```

## المجلد الأساسي `core`
يحتوي على الأدوات المشتركة التي تستخدمها جميع الميزات.
- `common`: ملفات عامة جدًا (Result, UiText, DispatcherProvider).
- `designsystem`: تصميم التطبيق الموحد (Theme, Colors, Typography, Components).
- `model`: النماذج المشتركة بين الطبقات.
- `network`: كل ما يتعلق بالاتصال بالخادم.
- `database`: قاعدة البيانات المحلية باستخدام Room.
- `storage`: للتعامل مع الملفات (تنزيل، رفع، Thumbnail).
- `media`: للمعاينة والتعامل مع الوسائط (Media3).
- `worker`: للمهام التي تستمر بعد إغلاق الشاشة (WorkManager).
- `error`: استثناءات التطبيق (AppException).
- `util`: أدوات مساعدة صغيرة.

## طبقة `data`
طبقة البيانات مسؤولة عن معرفة مصدر البيانات:
- `remote`: اتصال API (Retrofit / Ktor).
- `local`: التخزين المحلي وقواعد البيانات.
- `repository`: تطبيق الـ Interfaces الخاصة بالـ Repository.
- `mapper`: تحويل النماذج من DTO/Entity إلى Domain Model والعكس.

## طبقة `domain`
مسؤولة عن Use Cases المعقدة أو المشتركة بين أكثر من ViewModel.
- `repository`: الـ Interfaces الخاصة بالـ Repository.
- `usecase`: حالات الاستخدام مقسمة إلى مجلدات (project, script, asset, render, validation).

## طبقة `feature`
كل ميزة تحتوي على ملفاتها الخاصة المستقلة.
- `home`, `projects`, `create`, `storyboard`, `scene`, `editor`, `render`.
داخل كل ميزة: `Route`, `Screen`, `ViewModel`, `UiState`, `Action`, ومجلد `components`.

## مجلد التنقل `navigation`
يحتوي على إعدادات الـ Navigation Compose.
- `AppNavHost.kt`, `AppDestination.kt`

## قواعد مهمة للعمل
1. لا تستدعِ API من Composable: أرسل الحدث لـ ViewModel.
2. لا تضع منطق العمل داخل الواجهة: ضعه في Use Case أو Domain Model.
3. لا تجعل ViewModel يعرف تفاصيل Retrofit أو Room مباشرة: استخدم Repository كطبقة وسيطة.
4. كل شاشة لها: Route, Screen, ViewModel, UiState, Action, ومكونات خاصة.
5. النماذج القادمة من API ليست نماذج الواجهة: استخدم Mappers للتحويل بين DTO, Entity, و UiModel.
