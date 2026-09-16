# وثيقة بناء المشروع

## 1. فكرة التطبيق

**فكرة فيديو** هو تطبيق Android يسمح للمستخدم بإنشاء فيديو قصير من فكرة أو موضوع.

يدخل المستخدم:

- الفكرة.
- اللغة.
- مدة الفيديو.
- نوع القالب.
- مقاس الفيديو.

ثم ينشئ التطبيق:

- عنوانًا.
- نصًا قصيرًا.
- مشاهد متعددة.
- صورًا أو أصولًا يرفعها المستخدم.
- تعليقًا صوتيًا.
- ترجمة.
- فيديو MP4.

## 2. مثال استخدام

يدخل المستخدم:

> فوائد شرب الماء يوميًا

ينتج التطبيق:

1. عنوان: فوائد شرب الماء.
2. المشهد الأول: مقدمة قصيرة.
3. المشهد الثاني: فوائد الماء للجسم.
4. المشهد الثالث: علامات نقص الماء.
5. المشهد الرابع: نصيحة ختامية.
6. صوت عربي.
7. ترجمة عربية.
8. فيديو عمودي 9:16.

## 3. هدف النسخة الأولى

الهدف ليس بناء محرر فيديو ضخم، بل بناء مسار ناجح ومحدود:

```text
فكرة
↓
نص
↓
مشاهد
↓
صور
↓
صوت
↓
ترجمة
↓
FFmpeg
↓
MP4
```

## 4. ما يدخل في النسخة الأولى

- إنشاء مشروع.
- إدخال فكرة.
- توليد نص.
- تعديل النص.
- إنشاء مشاهد.
- رفع صور من الهاتف.
- توليد تعليق صوتي.
- إنشاء ترجمة.
- رندر فيديو.
- معاينة الفيديو.
- حفظ الفيديو.
- مشاركة الفيديو.

## 5. ما يؤجل

لا تضف في البداية:

- توليد فيديو AI كامل.
- Lip Sync.
- اتساق الشخصيات.
- استنساخ الصوت.
- Timeline احترافي متعدد المسارات.
- النشر التلقائي في TikTok وInstagram.
- التحليلات المتقدمة.
- الاشتراكات والمدفوعات.

هذه الوظائف تضاف بعد نجاح المسار الأساسي.

# المعمارية العامة

```text
┌──────────────────────────────┐
│ Android App                  │
│ Kotlin + Jetpack Compose    │
│ ViewModel + Repository       │
└──────────────┬───────────────┘
               │ REST / WebSocket
               ▼
┌──────────────────────────────┐
│ FastAPI Backend              │
│ Projects / Scenes / Jobs     │
│ Assets / Render / Providers  │
└───────┬──────────┬───────────┘
        │          │
        ▼          ▼
 PostgreSQL     Redis
        │          │
        └────┬─────┘
             ▼
┌──────────────────────────────┐
│ Workers                      │
│ LLM / TTS / Subtitle / FFmpeg│
└──────────────┬───────────────┘
               ▼
       MinIO / S3 Storage
```

تُستخدم المهام الخلفية لأن توليد الصوت والرندر قد يستغرقان وقتًا، ولا ينبغي إبقاء طلب HTTP مفتوحًا حتى اكتمال العملية. يوفر FastAPI آلية BackgroundTasks للعمليات التي يمكن تشغيلها بعد إرسال الاستجابة، لكن عمليات الرندر الطويلة الأفضل وضعها في Worker مستقل مثل Celery أو RQ [3][4].

# تطبيق Android

## التقنيات

| الوظيفة | التقنية |
|---|---|
| اللغة | Kotlin |
| الواجهات | Jetpack Compose |
| التنقل | Navigation Compose |
| إدارة الحالة | ViewModel + StateFlow |
| الشبكة | Retrofit أو Ktor Client |
| قاعدة محلية | Room |
| الإعدادات | DataStore |
| المهام الخلفية | WorkManager |
| تشغيل الفيديو | Media3 |
| الصور | Coil |
| الحقن | Hilt |
| الاختبارات | JUnit وCompose UI Test |

توصي Android بأن يكون ViewModel مسؤولًا عن منطق الشاشة، وأن تعرض الطبقة البيانات من خلال `StateFlow`، بينما تتولى composables عرض الحالة وإرسال الأحداث إلى ViewModel [1][2].

## بنية مجلد Android

```text
app/
├── core/
│   ├── common/
│   ├── network/
│   ├── database/
│   ├── storage/
│   └── ui/
│
├── data/
│   ├── local/
│   ├── remote/
│   ├── mapper/
│   └── repository/
│
├── domain/
│   ├── model/
│   └── usecase/
│
├── feature/
│   ├── home/
│   ├── projects/
│   ├── create/
│   ├── storyboard/
│   ├── editor/
│   ├── render/
│   └── settings/
│
└── MainActivity.kt
```

## الشاشات

### شاشة البداية

العناصر:

- شعار التطبيق.
- زر إنشاء فيديو.
- آخر المشاريع.
- حالات المشاريع.

### شاشة المشاريع

تعرض:

- اسم المشروع.
- صورة مصغرة.
- الحالة.
- تاريخ الإنشاء.
- زر فتح.
- زر حذف.

### شاشة إنشاء مشروع

الحقول:

```text
الفكرة
اللغة
المدة
نوع القالب
مقاس الفيديو
```

القوالب:

- تعليمي.
- قصة قصيرة.
- اقتباس.
- إعلان.
- معلومات عامة.
- منشور اجتماعي.

### شاشة السيناريو

تعرض:

- العنوان.
- النص العام.
- قائمة المشاهد.
- زر إعادة التوليد.
- زر اعتماد السيناريو.

### شاشة المشاهد

كل مشهد يحتوي:

- رقم المشهد.
- النص الصوتي.
- وصف الصورة.
- الصورة.
- مدة المشهد.
- زر استبدال الصورة.
- زر إعادة توليد الصوت.

### شاشة التصدير

تعرض:

- تقدم إنشاء الصوت.
- تقدم الترجمة.
- تقدم الرندر.
- رسائل الأخطاء.
- زر إعادة المحاولة.

### شاشة النتيجة

الأزرار:

- تشغيل الفيديو.
- حفظ الفيديو.
- مشاركة.
- إنشاء نسخة جديدة.
- تعديل المشروع.

# نموذج الحالة في Android

```kotlin
data class CreateProjectUiState(
    val idea: String = "",
    val language: String = "ar",
    val durationSeconds: Int = 30,
    val aspectRatio: String = "9:16",
    val template: String = "educational",
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## ViewModel

```kotlin
class CreateProjectViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState: StateFlow<CreateProjectUiState> = _uiState

    fun updateIdea(value: String) {
        _uiState.update { it.copy(idea = value) }
    }

    fun createProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                repository.createProject(
                    idea = _uiState.value.idea,
                    language = _uiState.value.language,
                    durationSeconds = _uiState.value.durationSeconds,
                    aspectRatio = _uiState.value.aspectRatio
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }
        }
    }
}
```

# Backend

## التقنيات

| الوظيفة | التقنية |
|---|---|
| API | FastAPI |
| اللغة | Python |
| قاعدة البيانات | PostgreSQL |
| ORM | SQLAlchemy |
| Migration | Alembic |
| Jobs | Celery أو RQ |
| Queue | Redis |
| التخزين | MinIO أو S3 |
| الرندر | FFmpeg وFFprobe |
| التحقق | Pydantic |
| التوثيق | JWT لاحقًا |
| الحاويات | Docker Compose |

من الأفضل تقسيم FastAPI إلى Routers وServices وRepositories بدل وضع كل شيء في ملف واحد؛ وثائق FastAPI تووضح أن التطبيقات الكبيرة تحتاج إلى بنية متعددة الملفات و`APIRouter` [5].

## بنية Backend

```text
backend/
├── app/
│   ├── main.py
│   ├── config.py
│   ├── database.py
│   │
│   ├── api/
│   │   ├── projects.py
│   │   ├── scenes.py
│   │   ├── assets.py
│   │   ├── jobs.py
│   │   └── renders.py
│   │
│   ├── models/
│   │   ├── project.py
│   │   ├── scene.py
│   │   ├── asset.py
│   │   ├── job.py
│   │   └── render.py
│   │
│   ├── schemas/
│   ├── repositories/
│   ├── services/
│   │   ├── project_service.py
│   │   ├── script_service.py
│   │   ├── voice_service.py
│   │   ├── subtitle_service.py
│   │   └── render_service.py
│   │
│   ├── providers/
│   │   ├── llm/
│   │   ├── tts/
│   │   ├── image/
│   │   └── subtitle/
│   │
│   └── workers/
│       ├── celery_app.py
│       ├── script_tasks.py
│       ├── voice_tasks.py
│       ├── subtitle_tasks.py
│       └── render_tasks.py
│
├── tests/
├── requirements.txt
└── Dockerfile
```

# قاعدة البيانات

## جدول projects

```text
id
title
idea
language
template
aspect_ratio
duration_seconds
status
created_at
updated_at
```

## جدول scenes

```text
id
project_id
position
narration
visual_prompt
duration_seconds
status
created_at
updated_at
```

## جدول assets

```text
id
project_id
scene_id
asset_type
storage_key
mime_type
file_size
checksum
provider
metadata_json
created_at
```

أنواع الأصول:

```text
IMAGE
AUDIO
VIDEO
SUBTITLE
THUMBNAIL
MUSIC
LOGO
```

## جدول jobs

```text
id
project_id
scene_id
job_type
status
progress
attempts
provider
provider_request_id
error_code
error_message
created_at
started_at
finished_at
```

## جدول renders

```text
id
project_id
status
output_key
format
width
height
duration_seconds
file_size
ffprobe_json
created_at
completed_at
```

# حالات المشروع

```text
DRAFT
SCRIPT_GENERATING
SCRIPT_READY
SCENES_READY
ASSETS_GENERATING
READY_TO_RENDER
RENDERING
COMPLETED
FAILED
```

# حالات Job

```text
QUEUED
RUNNING
SUCCEEDED
FAILED
RETRYING
CANCELLED
```

القاعدة المهمة:

> لا تستخدم `SUCCEEDED` أو `COMPLETED` إلا بعد التأكد من وجود الملف والتحقق منه بواسطة FFprobe.

# واجهات API

## إنشاء مشروع

```http
POST /api/v1/projects
```

الطلب:

```json
{
  "idea": "فوائد شرب الماء",
  "language": "ar",
  "template": "educational",
  "duration_seconds": 30,
  "aspect_ratio": "9:16"
}
```

الاستجابة:

```json
{
  "id": "project_123",
  "status": "DRAFT"
}
```

## إنشاء السيناريو

```http
POST /api/v1/projects/project_123/script
```

الاستجابة:

```json
{
  "job_id": "job_456",
  "status": "QUEUED"
}
```

## الاستعلام عن Job

```http
GET /api/v1/jobs/job_456
```

الاستجابة:

```json
{
  "id": "job_456",
  "type": "SCRIPT_GENERATION",
  "status": "RUNNING",
  "progress": 60,
  "message": "جاري إنشاء المشاهد"
}
```

## إنشاء الرندر

```http
POST /api/v1/projects/project_123/render
```

الاستجابة:

```json
{
  "job_id": "job_789",
  "status": "QUEUED"
}
```

## أحداث مباشرة

```text
WS /api/v1/projects/{project_id}/events
```

مثال:

```json
{
  "event": "JOB_PROGRESS",
  "job_id": "job_789",
  "progress": 75,
  "message": "جاري دمج الصوت والترجمة"
}
```

# عقدة LLM

يجب ألا يطلب Backend نصًا حرًا من النموذج. اطلب JSON محددًا.

## المدخل

```json
{
  "idea": "فوائد شرب الماء",
  "language": "ar",
  "duration_seconds": 30,
  "template": "educational"
}
```

## المخرج

```json
{
  "title": "فوائد شرب الماء",
  "description": "فيديو قصير يوضح أهمية شرب الماء",
  "scenes": [
    {
      "position": 1,
      "narration": "هل تعلم أن الماء ضروري لصحة جسمك؟",
      "visual_prompt": "كوب ماء على طاولة بإضاءة طبيعية",
      "duration_seconds": 6
    },
    {
      "position": 2,
      "narration": "يساعد الماء على تحسين التركيز وتنظيم حرارة الجسم.",
      "visual_prompt": "شخص يعمل بنشاط ويشرب الماء",
      "duration_seconds": 8
    }
  ]
}
```

## قواعد التحقق

- عدد المشاهد بين 3 و8.
- مدة كل مشهد أكبر من صفر.
- النص غير فارغ.
- مجموع المدد قريب من مدة الفيديو.
- لا توجد حقول غير معروفة.
- اللغة مدعومة.
- لا يسمح النموذج بإنشاء روابط ملفات غير موثوقة.

# مزودات الذكاء الاصطناعي

استخدم Interface موحدًا:

```python
class LLMProvider(Protocol):
    async def generate_script(
        self,
        idea: str,
        language: str,
        duration_seconds: int,
        template: str
    ) -> dict:
        ...
```

مزودات مقترحة:

```text
OpenAIProvider
OllamaProvider
LiteLLMProvider
```

لذلك تستطيع تغيير المزود دون تغيير `ProjectService`.

## TTS Provider

```python
class TTSProvider(Protocol):
    async def synthesize(
        self,
        text: str,
        language: str,
        voice: str
    ) -> AssetResult:
        ...
```

ابدأ بمزود واحد مثل Kokoro، ثم أضف F5-TTS أو CosyVoice لاحقًا.

# خط التنفيذ

## المرحلة الأولى: إنشاء النص

```text
Project Created
↓
Create Script Job
↓
LLM Provider
↓
Validate JSON
↓
Save Scenes
↓
SCRIPT_READY
```

## المرحلة الثانية: تجهيز الأصول

```text
Scene
↓
User Upload أو Image Provider
↓
Validate Asset
↓
Save Asset
↓
SCENES_READY
```

## المرحلة الثالثة: الصوت

```text
Scene Narration
↓
TTS Provider
↓
WAV/MP3
↓
Save Asset
```

## المرحلة الرابعة: الترجمة

```text
Audio
↓
Whisper/faster-whisper
↓
SRT/ASS
↓
Save Subtitle Asset
```

## المرحلة الخامسة: الرندر

```text
Images + Audio + Subtitles
↓
Generate FFmpeg Inputs
↓
Render
↓
FFprobe
↓
Upload MP4
↓
COMPLETED
```

# بناء الفيديو بواسطة FFmpeg

الخط العام:

```text
الصورة الأولى + مدة
الصورة الثانية + مدة
الصورة الثالثة + مدة
الصوت
الترجمة
الشعار
```

أمثلة عمليات الرندر التي يحتاجها النظام:

- تغيير المقاس إلى 1080×1920.
- ملء الشاشة مع قص الحواف.
- إضافة حركة Zoom بسيطة للصورة.
- دمج الصوت.
- إضافة ترجمة SRT أو ASS.
- دمج شعار PNG.
- إنشاء Thumbnail.
- فحص الملف النهائي بـFFprobe.

لا ينبغي تنفيذ هذه العمليات داخل Android في النسخة الأولى. ينفذها Backend Worker، بينما يعرض Android التقدم والنتيجة.

# التخزين

استخدم MinIO محليًا وS3 في الإنتاج.

```text
bucket/
├── projects/{project_id}/
│   ├── images/
│   ├── audio/
│   ├── subtitles/
│   ├── renders/
│   └── thumbnails/
```

لا تخزن الملفات الكبيرة داخل PostgreSQL. خزّن المسار والبيانات الوصفية فقط.

# الأمان

- ضع مفاتيح AI في Backend فقط.
- لا تضع API Keys داخل APK.
- افحص امتداد MIME وحجم الملف.
- استخدم أسماء ملفات عشوائية.
- امنع مسارات الملفات القادمة من المستخدم.
- استخدم روابط تنزيل مؤقتة.
- ضع حدًا لحجم الفيديو.
- ضع Rate Limit لطلبات التوليد.
- سجل كل عملية باستخدام `job_id`.
- لا تسمح برفع ملفات تنفيذية.

# معالجة الأخطاء

## أخطاء قابلة لإعادة المحاولة

- انتهاء مهلة الاتصال.
- توقف مزود مؤقتًا.
- خطأ Redis.
- خطأ مؤقت في التخزين.
- انقطاع الشبكة.

## أخطاء غير قابلة لإعادة المحاولة تلقائيًا

- مفتاح API غير صحيح.
- ملف صوت تالف.
- JSON غير صالح بعد عدة محاولات.
- مساحة تخزين غير كافية.
- صيغة غير مدعومة.

## رسالة المستخدم

بدلًا من:

```text
ProviderError: HTTP 429
```

اعرض:

```text
الخدمة مشغولة حاليًا. سنعيد المحاولة تلقائيًا.
```

# Docker Compose

```yaml
services:
  api:
    build: ./backend
    ports:
      - "8000:8000"
    depends_on:
      - postgres
      - redis
      - minio

  worker:
    build: ./backend
    command: celery -A app.workers.celery_app worker --loglevel=info
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: video_factory
      POSTGRES_USER: app
      POSTGRES_PASSWORD: change_me
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7

  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data

volumes:
  postgres_data:
  minio_data:
```

# خطة التطوير

## المرحلة الأولى

- إنشاء مشروع Android.
- إعداد Compose.
- إعداد Navigation.
- إنشاء شاشة المشاريع.
- إنشاء شاشة إنشاء مشروع.
- إضافة قاعدة Room.

## المرحلة الثانية

- إعداد FastAPI.
- إعداد PostgreSQL.
- إنشاء Project API.
- ربط Android بالخادم.
- إضافة حالة المشروع.

## المرحلة الثالثة

- إضافة توليد السيناريو.
- إضافة Scene Editor.
- إضافة JSON Validation.
- إضافة Jobs.

## المرحلة الرابعة

- إضافة رفع الصور.
- إضافة MinIO.
- إضافة TTS.
- إضافة إنشاء الترجمة.

## المرحلة الخامسة

- إضافة FFmpeg Worker.
- إضافة FFprobe.
- إضافة تقدم الرندر.
- إضافة تنزيل MP4.

## المرحلة السادسة

- الاختبارات.
- معالجة الأخطاء.
- تحسين تجربة المستخدم.
- إنشاء APK تجريبي.

# الاختبارات

## اختبارات Android

- إنشاء مشروع.
- تعديل الفكرة.
- عرض حالة Job.
- التعامل مع انقطاع الشبكة.
- استكمال التنزيل.
- تشغيل الفيديو.
- مشاركة الفيديو.

## اختبارات Backend

- إنشاء مشروع.
- إنشاء مشاهد.
- التحقق من JSON.
- تغيير حالات Job.
- إعادة المحاولة.
- رفض الملفات الكبيرة.
- رفض الامتدادات الخطرة.
- التحقق من وجود ملف الرندر.

## اختبار نهاية إلى نهاية

```text
إنشاء مشروع
→ توليد نص
→ حفظ مشاهد
→ رفع صورة
→ توليد صوت
→ إنشاء ترجمة
→ رندر MP4
→ FFprobe
→ تنزيل الفيديو
→ تشغيل الفيديو
```

# معايير نجاح النسخة الأولى

- يستطيع المستخدم إنشاء فيديو كامل دون تدخل مطور.
- لا تضيع البيانات عند إغلاق التطبيق.
- تظهر حالة كل عملية بوضوح.
- لا تظهر عملية مكتملة بدون ملف فعلي.
- يمكن إعادة توليد مشهد واحد فقط.
- يمكن تنزيل الفيديو ومشاركته.
- يدعم الفيديو العمودي 9:16.
- يدعم اللغة العربية بشكل صحيح.
- يعمل المشروع محليًا بواسطة Docker Compose.

# المشاريع مفتوحة المصدر المستخدمة

ابدأ بهذه المشاريع فقط:

1. [Jetpack Compose Samples](https://github.com/android/compose-samples) لبناء واجهة Android [6].
2. [Media3](https://github.com/androidx/media) لمعاينة الفيديو [7].
3. [FastAPI](https://github.com/fastapi/fastapi) لبناء Backend [8].
4. [PostgreSQL](https://github.com/postgres/postgres) لقاعدة البيانات.
5. [Redis](https://github.com/redis/redis) للطوابير.
6. [Celery](https://github.com/celery/celery) للمهام الخلفية.
7. [MinIO](https://github.com/minio/minio) للتخزين.
8. [FFmpeg](https://github.com/FFmpeg/FFmpeg) للرندر.
9. [Kokoro](https://github.com/hexgrad/kokoro) للصوت [9].
10. [faster-whisper](https://github.com/SYSTRAN/faster-whisper) للتفريغ والترجمة.
11. [Ollama](https://github.com/ollama/ollama) لتشغيل LLM محليًا [10].
12. [Open Video Editor](https://github.com/devhyper/open-video-editor) كمرجع لتصميم محرر Android [11].

# القرار المعماري النهائي

المشروع يجب أن يكون **Provider-Neutral**.

أي أن التطبيق لا يعتمد مباشرة على مزود واحد:

```text
Android
  ↓
FastAPI
  ↓
Provider Interface
  ├── OpenAI أو Ollama
  ├── Kokoro أو F5-TTS
  ├── ComfyUI أو مزود صور
  ├── Whisper أو faster-whisper
  └── FFmpeg
```

بهذا الشكل يمكنك تغيير نموذج الذكاء الاصطناعي أو مزود الصوت دون إعادة بناء تطبيق Android. كما أن فصل `Projects` و`Scenes` و`Assets` و`Jobs` و`Renders` يجعل المشروع قابلًا للتوسع لاحقًا إلى Character Consistency وLip Sync والنشر الاجتماعي.
