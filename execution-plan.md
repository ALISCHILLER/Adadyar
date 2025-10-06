# برنامه اجرایی فاز شروع «عددیار»

این سند ادامهٔ نقشه‌راه اصلی است و حلقه‌های گمشدهٔ ورود به فاز اجرا را پر می‌کند: بک‌لاگ دو هفتهٔ اول، ماشین‌حالت‌های MVI برای صفحات کلیدی، قراردادهای داده، ماژول‌های Koin و چک‌لیست‌های کیفیت.

---

## ۱. بک‌لاگ اجرایی هفتهٔ ۱ و ۲ (به‌همراه معیار پذیرش)

### هفتهٔ ۱ — راه‌اندازی زیرساخت و لایهٔ دامنه

| روز | کارها | معیار پذیرش |
| --- | ------ | ------------- |
| ۱ | ایجاد ماژول‌ها (`:app`, `:core-ui`, `:core-common`, `:core-storage`, `:domain`, `:data-content`, `:data-progress`, `:features:*`)؛ پیاده‌سازی تم، فونت فارسی، RTL و ناوبری ریشهٔ خالی | اپلیکیشن اجرا می‌شود؛ فونت فارسی و RTL اعمال شده؛ NavHost بدون محتوای واقعی کار می‌کند |
| ۲ | تعریف موجودیت‌ها و قراردادهای مخزن در `:domain` (Grade, Chapter, Lesson, Exercise, UserProgress, Profile, RubricLevel) و UseCaseهای اصلی (`GetGrades`, `GetChaptersByGrade`, `GetLesson`, `CompleteLesson`, `UpdateRubric`, `GetUserProgress`) | تمام UseCaseها امضای مشخص دارند و هیچ وابستگی Androidی در دامنه نیست |
| ۳ | ساخت `:core-storage`: فراهم‌کننده‌های JsonParser (kotlinx-serialization)، DataStoreFactory و DatabaseFactory (Room) | همهٔ Providerها به‌صورت singleton از Koin Resolve می‌شوند |
| ۴ | توسعهٔ `:data-content`: `ContentLocalDataSource` برای خواندن `assets/lessons/...`، DTOها، Mapperها و `ContentRepositoryImpl` | فراخوانی `GetLesson` روی نمونهٔ JSON خروجی دامنهٔ تمیز را بازمی‌گرداند |
| ۵ | پیاده‌سازی `:data-progress`: DataStore تنظیمات + Room (اسکلت DB و DAOها) و `ProgressRepositoryImpl` | اجرای `CompleteLesson(lessonId, rubric)` رکورد idempotent ثبت می‌کند (بدون رکورد تکراری) |

### هفتهٔ ۲ — اتصال فیچرها و محتوا

| روز | کارها | معیار پذیرش |
| --- | ------ | ------------- |
| ۱ | ایجاد `:features:home` با UI پایهٔ Home/Grade/Chapter، ViewModel و Store ساده | لیست پایه/فصل از `assets` بارگذاری و پیشرفت ۰٪ نمایش داده می‌شود |
| ۲ | ساخت `:features:lesson` با Store مبتنی بر MVI (`LessonIntent`, `LessonResult`, `LessonReducer`, `LessonUiState`, `LessonUiEffect`) | `Load(lessonId)` درس را بار می‌کند، فاز روی Diagnose و `hintsLeft = 3` است و قانون ۷ کلمه‌ای دیده می‌شود |
| ۳ | افزودن کامپوننت‌های `:core-ui`: `PhaseCard`, `HintStepper (H1/H2/H3)`, `RubricBar (0–3)`, `ProgressRing` | HintStepper سطح‌ها را به‌ترتیب آزاد می‌کند و H2 قبل از H1 در دسترس نیست |
| ۴ | اتصال UseCaseهای `UpdateRubric` و `CompleteLesson` به Room/DataStore و پیاده‌سازی افکت‌ها (Toast/Confetti) به‌صورت `UiEffect` | تکمیل درس فقط یک‌بار کنفتی پخش می‌کند و اثر مصرف می‌شود |
| ۵ | بهینه‌سازی اولیهٔ عملکرد و دسترس‌پذیری: lazy image loading، لمس ≥ ۴۸dp، contentDescription | باز شدن صفحهٔ درس < ۳۰۰ms روی دستگاه میان‌رده؛ TalkBack متن کلیدی را می‌خواند |

---

## ۲. ماشین‌حالت‌های MVI برای صفحات کلیدی

### ۲.۱ LessonScreen (چارچوب 7D)

| Intent | Action / UseCase | Result | Reduce (State ← Result) | Effect |
| ------ | ---------------- | ------ | ----------------------------- | ------ |
| `Load(lessonId)` | `GetLesson` | `Loaded(lesson)` | `lesson = …`, `phase = Diagnose`, `hintsLeft = 3`, `error = null` | — |
| `StartPhase(phase)` | اعتبارسنجی ترتیب فازها | `PhaseChanged(phase)` | `phase = phase` | — |
| `ToggleHint(level)` | کاهش شمارندهٔ هینت | `HintServed(level)` | `hintsLeft--`, `lastHint = level` | `ShowToast("راهنمای سطح " + level)` |
| `UpdateRubric(level)` | `UpdateRubric` | `RubricSaved(level)` | `rubricLevel = level` | — |
| `CompleteLesson` | `CompleteLesson` | `Completed` | `completed = true` | `ShowConfetti` |
| `Retry` | اجرای مجدد آخرین اکشن | `Loaded / …` | State به آخرین نقطه برمی‌گردد | — |
| خطاها | — | `Error(type)` | `error = type` | `ShowToast(msg)` |

> قوانین: Reducer خالص و بدون Side-Effect؛ Effectها one-shot (SharedFlow).

### ۲.۲ PracticeScreen

| Intent | Action / UseCase | Result | Reduce | Effect |
| ------ | ---------------- | ------ | ------ | ------ |
| `Load(lessonId)` | بارگذاری تمرین‌ها | `ExercisesLoaded(list)` | `current = 0`, `score = 0`, `error = null` | — |
| `Answer(exerciseId, value)` | `SubmitAnswer` | `AnswerChecked(correct)` | به‌روزرسانی نتیجه، نمره و ثبت پاسخ | `Vibrate(correct)` |
| `RequestHint(level)` | قوانین هینت | `HintServed(level)` | شمارش مصرف هینت | `ShowToast` |
| `Next` | تغییر اندیس | `Moved(nextIndex)` | `current = nextIndex` | — |
| `Exit` | ذخیرهٔ وضعیت | `Saved` | — | `NavigateBack` |
| خطاها | — | `Error(type)` | `error = type` | `ShowToast(msg)` |

---

## ۳. قرارداد داده

### ۳.۱ JSON Schema خلاصهٔ محتوای درس

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "zarmath.lesson.v1",
  "type": "object",
  "required": [
    "lesson_id",
    "grade",
    "chapter_id",
    "title",
    "core_rule",
    "phases",
    "hints",
    "common_error"
  ],
  "properties": {
    "schema": { "type": "string" },
    "generated_at": { "type": "string" },
    "localization": {
      "type": "object",
      "properties": {
        "lang": { "type": "string" },
        "rtl": { "type": "boolean" }
      }
    },
    "lesson_id": { "type": "string", "pattern": "^g[1-6]_ch[1-9]\\d*_l\\d+$" },
    "grade": { "type": "integer", "minimum": 1, "maximum": 6 },
    "chapter_id": { "type": "integer", "minimum": 1 },
    "title": { "type": "string", "minLength": 3 },
    "core_rule": { "type": "string", "minLength": 5, "maxLength": 80 },
    "estimated_duration_min": { "type": "integer", "minimum": 5, "maximum": 25 },
    "phases": {
      "type": "object",
      "required": [
        "diagnose",
        "concrete",
        "pictorial",
        "abstract",
        "practice",
        "debrief",
        "deepen"
      ],
      "properties": {
        "diagnose": {
          "type": "object",
          "properties": {
            "questions": {
              "type": "array",
              "minItems": 1,
              "items": { "type": "string" }
            }
          }
        },
        "concrete": {
          "type": "object",
          "properties": {
            "description": { "type": "string" },
            "image_path": { "type": "string" }
          }
        },
        "pictorial": {
          "type": "object",
          "properties": {
            "description": { "type": "string" },
            "image_path": { "type": "string" }
          }
        },
        "abstract": {
          "type": "object",
          "properties": {
            "description": { "type": "string" }
          }
        },
        "practice": {
          "type": "object",
          "properties": {
            "guided": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "prompt": { "type": "string" },
                  "hint": { "type": "string" }
                }
              }
            },
            "independent": {
              "type": "array",
              "items": { "type": "string" }
            }
          }
        },
        "debrief": { "type": "string" },
        "deepen": { "type": "string" }
      }
    },
    "hints": {
      "type": "object",
      "required": ["h1", "h2", "h3"],
      "properties": {
        "h1": { "type": "string" },
        "h2": { "type": "string" },
        "h3": { "type": "string" }
      }
    },
    "common_error": {
      "type": "object",
      "required": ["description", "correction"],
      "properties": {
        "description": { "type": "string" },
        "correction": { "type": "string" }
      }
    },
    "materials": { "type": "string" }
  }
}
```

### ۳.۲ کلیدهای DataStore (Preferences)

| کلید | نوع | توضیح |
| ---- | --- | ------ |
| `ui.theme` | `light \| dark \| system` | انتخاب تم |
| `ui.difficulty` | `easy \| normal \| hard` | سطح سختی تمرین |
| `user.last_profile_id` | String | آخرین پروفایل فعال |
| `user.last_lesson_id` | String | آخرین درس باز |
| `content.version` | String | نسخهٔ محتوا (همگام با `metadata.json`) |
| `onboarding.done` | Boolean | وضعیت تکمیل راه‌اندازی اولیه |

### ۳.۳ طرح جداول Room

#### جدول `progress`

| ستون | نوع | توضیح |
| ----- | --- | ------ |
| `id` | Integer (PK, auto) | شناسهٔ یکتا |
| `profile_id` | Text (Indexed) | شناسهٔ پروفایل |
| `lesson_id` | Text (Indexed, Unique با `profile_id`) | شناسهٔ درس |
| `rubric_level` | Integer (۰ تا ۳) | سطح روبریک ثبت‌شده |
| `is_completed` | Boolean | وضعیت تکمیل |
| `completed_at` | Long (epoch millis) | زمان تکمیل |

> محدودیت: ترکیب `(profile_id, lesson_id)` یکتا است.

#### جدول `exercise_result`

| ستون | نوع | توضیح |
| ----- | --- | ------ |
| `id` | Integer (PK, auto) | شناسهٔ یکتا |
| `profile_id` | Text (Indexed) | شناسهٔ پروفایل |
| `lesson_id` | Text (Indexed) | شناسهٔ درس |
| `exercise_id` | Text | شناسهٔ تمرین |
| `correct` | Boolean | درست بودن پاسخ |
| `duration_ms` | Long | مدت پاسخ |
| `hint_level_used` | Integer (۰–۳) | سطح هینت مصرف‌شده |
| `ts` | Long (epoch millis) | زمان ثبت |

> حذف پروفایل ⇒ حذف Cascading نتایج مرتبط.

---

## ۴. نقشهٔ ماژول‌های Koin

* `commonModule`: Logger، JsonParser، Clock و ImageLoader (singleton)
* `storageModule`: DataStore، پایگاه‌دادهٔ Room و DAOها (singleton)
* `dataModule`: `ContentLocalDataSource`، `ProgressLocalDataSource`، `ContentRepositoryImpl` و `ProgressRepositoryImpl` (singleton)
* `domainModule`: UseCaseها (factory)
* `presentationModule`: ViewModelها و Mapperهای UI (scope viewModel)

---

## ۵. چک‌لیست‌های کیفیت (Definition of Done)

### Content Loader

- [ ] `metadata.json` خوانده و درس‌ها ایندکس می‌شوند.
- [ ] نبود تصویر با حالت خطا و امکان `retry` مدیریت می‌شود.
- [ ] تغییر `content.version` باعث ایندکس مجدد می‌شود.

### Lesson (7D)

- [ ] هفت کارت فازها رندر می‌شوند.
- [ ] HintStepper سطحی عمل می‌کند (H2 پس از H1).
- [ ] روبریک ۰–۳ ذخیره و بازیابی می‌شود.
- [ ] تکمیل درس فقط یک‌بار `ShowConfetti` ایجاد می‌کند.

### Practice

- [ ] چرخهٔ Example → Problem ×۲ بدون باگ اجرا می‌شود.
- [ ] پاسخ، زمان و سطح هینت در Room ذخیره می‌شود.
- [ ] `Exit` وضعیت را ذخیره و به صفحهٔ قبل بازمی‌گردد.

### A11y & Performance

- [ ] لمس‌ها ≥ ۴۸dp و کنتراست سطح AA است.
- [ ] شروع سرد < ۱.۵ ثانیه و باز شدن درس < ۳۰۰ms.
- [ ] تصاویر فشرده (WebP/AVIF) و بارگذاری تنبل دارند.

---

## ۶. ریسک‌ها و راهکارهای مهار

| ریسک | پیامد | راهکار |
| ----- | ------ | ------- |
| حجم بالا | بستهٔ نصب بزرگ | فشرده‌سازی تصاویر، حذف منابع تکراری، lazy loading |
| افت عملکرد | تجربهٔ کاربری ضعیف | کش داخلی، کاهش انیمیشن‌های سنگین، اندازه‌گیری منظم پروفایل |
| بدفهمی مفاهیم (به‌ویژه کسر/درصد) | افت یادگیری | تقویت فازهای Concrete و Pictorial و افزودن سناریوهای Near-Transfer |
| پیچیدگی State | باگ‌های UI | پایبندی به State واحد، Reducer خالص و Effectهای one-shot |

---

## قدم بعدی پیشنهادی

تهیهٔ «نمونهٔ طلایی» (Golden Lesson) برای یک درس پایهٔ اول که تمام فازهای 7D، هینت‌های سه‌سطحی و روبریک را شامل شود تا تست End-to-End روی زیرساخت تازه فراهم‌شده اجرا شود.
