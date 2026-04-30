# 🛠️ EDUKO Backend — خطة التوافق مع الفرونت

> **آخر تحديث:** 30 أبريل 2026  
> **الهدف:** تعديل الـ Backend ليتوافق تماماً مع ما يتوقعه كل من EDUKO (Student Platform) و EDUKO-ADMIN بدون إعادة بناء كاملة.

---

## 📋 جدول الأولويات

| # | المهمة | الأولوية | التأثير | الوقت التقديري |
|---|--------|----------|---------|----------------|
| 1 | إصلاح Login/Signup Response | 🔴 Critical | يوقف الـ app كله | 1-2 ساعة |
| 2 | إصلاح Signup — Fields الاختيارية | 🔴 Critical | مش قادر يسجل | 30 دقيقة |
| 3 | إصلاح EnrollRequest — courseId | 🔴 Critical | مش قادر يشترك | 1 ساعة |
| 4 | إصلاح EnrollResponse — كامل | 🔴 Critical | الـ UI مكسور | 30 دقيقة |
| 5 | إصلاح CourseDto — fields ناقصة | 🟠 High | صفحة الكورسات فارغة | 1-2 ساعة |
| 6 | إصلاح Password Reset Flow | 🟠 High | ثغرة أمنية | 2 ساعة |
| 7 | RESTful Endpoint Names | 🟡 Medium | consistency | 30 دقيقة |
| 8 | إضافة Progress Endpoints | 🟡 Medium | feature ناقصة | 3-4 ساعات |
| 9 | إضافة Swagger Annotations | 🟡 Medium | توثيق | 1 ساعة |
| 10 | إضافة CORS Configuration | 🟡 Medium | deployment | 30 دقيقة |

---

## 🔴 Phase 1 — Critical Fixes (يجب يتعمل الأول)

### ✅ Task 1 — إصلاح Login & Signup Response

**المشكلة:**

`Response.java` حالياً بترجع:
```json
{ "name": "Ahmed", "token": "eyJ..." }
```

الفرونت بيتوقع:
```json
{
  "token": "eyJ...",
  "user": {
    "id": 1,
    "name": "Ahmed",
    "email": "ahmed@gmail.com",
    "role": "student",
    "avatar": null
  }
}
```

**الحل:**

**خطوة 1 — أنشئ `AuthResponse.java` جديد بدل `Response.java`:**
```java
// src/main/java/com/springboot/eduko/controller/vms/AuthResponse.java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserPayload user;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class UserPayload {
        private Long   id;
        private String name;
        private String email;
        private String role;    // "student" | "teacher"
        private String avatar;  // nullable
    }
}
```

**خطوة 2 — غيّر `AuthServiceImpl.java` في دالة `login()`:**
```java
@Override
public AuthResponse login(LoginRequest loginRequest) {
    BaseUserDto baseUserDto = baseUserService.getUserByEmail(loginRequest.getEmail());
    if (Objects.isNull(baseUserDto))
        throw new RuntimeException("email.not.exist");
    if (!passwordEncoder.matches(loginRequest.getPassword(),
            baseUserDto.getPassword().replace("{bcrypt}", "")))
        throw new RuntimeException("Invalid.password");

    String name   = "";
    String role   = "student";
    Long   userId = baseUserDto.getId();

    if (baseUserDto.getStudent() != null) {
        name = studentService.getStudentNameById(baseUserDto.getStudent().getId());
        role = "student";
    } else if (baseUserDto.getTeacher() != null) {
        name = teacherService.getTeacherNameById(baseUserDto.getTeacher().getId());
        role = "teacher";
    }

    String token = handleToken.generateToken(baseUserDto);
    AuthResponse.UserPayload payload =
        new AuthResponse.UserPayload(userId, name, baseUserDto.getEmail(), role, null);
    return new AuthResponse(token, payload);
}
```

**خطوة 3 — نفس التعديل في `signupForStudent()` و `signupForTeacher()`:**
```java
// في signupForStudent() — آخر سطر
// قبل
return new Response(baseUserDto2.getStudent().getFirstName(), handleToken.generateToken(baseUserDto2));

// بعد
String name = baseUserDto2.getStudent().getFirstName();
String token = handleToken.generateToken(baseUserDto2);
AuthResponse.UserPayload payload =
    new AuthResponse.UserPayload(baseUserDto2.getId(), name, baseUserDto2.getEmail(), "student", null);
return new AuthResponse(token, payload);
```

**خطوة 4 — غيّر الـ return type في `SecurityController.java`:**
```java
@PostMapping({"/login", "/auth/login"})
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) throws URISyntaxException {
    return ResponseEntity.ok(authService.login(loginRequest));
}

@PostMapping({"/signup", "/auth/register"})
public ResponseEntity<AuthResponse> signup(@RequestBody @Valid SignupRequestForStudent req) throws URISyntaxException {
    return ResponseEntity.created(new URI("/auth/register")).body(authService.signupForStudent(req));
}

@PostMapping({"/signupForTeacher", "/auth/register/teacher"})
public ResponseEntity<AuthResponse> signupForTeacher(@RequestBody @Valid SignupRequestForTeachers req) throws URISyntaxException {
    return ResponseEntity.created(new URI("/auth/register/teacher")).body(authService.signupForTeacher(req));
}
```

---

### ✅ Task 2 — إصلاح Signup — Fields اختيارية

**المشكلة:**
`SignupRequestForStudent.java` كل fields فيها `@NotBlank`. الفرونت بيبعت فقط:
```json
{ "firstName": "Ahmed", "lastName": "Ali", "email": "...", "password": "..." }
```
الباك بيرفض لأن `parentName`, `parentNumber`, `studentNumber` غايبين.

**الحل — شيل `@NotBlank` من الـ 3 fields:**
```java
// قبل
@NotBlank(message = "Parent name is required")
@Pattern(regexp = "^[a-zA-Z ]+$", message = "...")
private String parentName;

@NotBlank(message = "Parent phone number is required")
@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "...")
private String parentNumber;

@NotBlank(message = "StudentData number is required")
@Pattern(regexp = "\\d+", message = "...")
@Size(min = 6, max = 10, message = "...")
private String studentNumber;

// بعد — optional
private String parentName;
private String parentNumber;
private String studentNumber;
```

> ⚠️ خليهم optional في signup وممكن تطلبهم في صفحة إكمال البروفايل لاحقاً.

---

### ✅ Task 3 — إصلاح EnrollRequest

**المشكلة:**
الفرونت بيبعت `{ "courseId": 1 }` — الباك بيتوقع `{ "studentEmail": "...", "courseTitle": "..." }`.

**الحل:**
```java
// EnrollRequest.java بعد التعديل
public class EnrollRequest {
    // studentEmail شيله — الطالب authenticated من JWT
    private Long courseId;
}
```

**في `EnrollmentServiceImpl` غيّر الـ logic:**
```java
@Override
public EnrollResponse doEnrollments(EnrollRequest request) {
    // اسحب الطالب من Security Context
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    BaseUser baseUser = baseUserRepo.findBaseUsersByEmail(email);
    if (baseUser == null) throw new RuntimeException("user.not.found");

    // ابحث بـ courseId مش courseTitle
    EduCourses course = courseRepo.findById(request.getCourseId())
        .orElseThrow(() -> new RuntimeException("course.not.found"));

    // ... باقي الـ enrollment logic
}
```

---

### ✅ Task 4 — إصلاح EnrollResponse

**المشكلة:**
`EnrollResponse` بترجع `{ "status": "enrolled" }` — الفرونت بيتوقع object كامل.

**الحل:**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollResponse {
    private Long   id;
    private Long   courseId;
    private String courseTitle;
    private String enrolledAt;   // ISO-8601
    private String status;       // "active" | "completed" | "expired"
}
```

---

## 🟠 Phase 2 — High Priority Fixes

### ✅ Task 5 — إصلاح CourseDto و CourseResponse

**المشكلة:**
`CourseDto` بيرجع `{ id, courseTitle, courseLink, lectures }` فقط.

الفرونت بيتوقع على `GET /courses`:
```json
{
  "id": 1,
  "title": "Java for Beginners",
  "description": "...",
  "thumbnailUrl": "https://...",
  "price": 299.00,
  "currency": "EGP",
  "level": "beginner",
  "category": "Tech",
  "instructorName": "Dr. Ahmed",
  "rating": 4.5,
  "enrolledCount": 120,
  "status": "published"
}
```

**الحل — عدّل `CourseDto.java`:**
```java
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDto {
    private Long    id;
    private String  courseTitle;
    private String  courseLink;
    private String  description;
    private String  thumbnailUrl;
    private Double  price;
    private String  currency;         // "EGP" | "USD"
    private String  level;            // "beginner" | "intermediate" | "advanced"
    private String  category;
    private String  instructorName;
    private Double  rating;
    private Integer enrolledCount;
    private String  status;           // "published" | "draft" | "archived"
    @JsonIgnore
    private List<Enrollments> enrollments;
    @JsonIgnore
    private Teacher teacher;
    private List<LectureDto> lectures; // يظهر فقط في getCourseById
}
```

**وفي `EduCourses.java` (Model) أضف الـ columns الجديدة:**
```java
@Column
private String  description;
@Column
private String  thumbnailUrl;
@Column
private Double  price;
@Column
private String  currency;      // "EGP" | "USD"
@Column
private String  level;         // "beginner" | "intermediate" | "advanced"
@Column
private String  category;
@Column
private Double  rating;
@Column
private Integer enrolledCount;
@Column
private String  status;        // "published" | "draft" | "archived"
```

> ⚠️ بعد إضافة الـ columns لازم تعمل `schema update` أو migration عشان الـ DB يتحدث.

---

### ✅ Task 6 — إصلاح Password Reset — Security Fix

**الثغرة الحالية:**
`PUT /resetPass { email, newPassword }` — أي شخص يعرف الـ email يقدر يغير الباسورد مباشرة!

**الـ Flow الصح (Two-Step):**
```
1. POST /auth/forgot-password  { "email": "..." }                     → { "resetToken": "..." }
2. PUT  /auth/reset-password   { "token": "...", "newPassword": "..." } → { message: "success" }
```

**في `SecurityController.java` أضف الـ endpoint الجديد:**
```java
@PostMapping({"/forgot-password", "/auth/forgot-password"})
public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
    BaseUser user = baseUserRepo.findBaseUsersByEmail(req.getEmail());
    if (user == null) throw new RuntimeException("user.not.found");
    String token = handleToken.generateTokenForResetPassword(baseUserMapper.toDto(user));
    // في production: ابعت على الـ email. حالياً: ارجعه في response.
    return ResponseEntity.ok(Map.of("resetToken", token));
}

@PutMapping({"/reset-password", "/auth/reset-password"})
public ResponseEntity<ResetPassword> resetPassword(@RequestBody ResetPasswordRequest req) {
    BaseUserDto userDto = handleToken.validateToken(req.getToken());
    if (userDto == null) throw new RuntimeException("invalid.or.expired.token");
    BaseUser user = baseUserRepo.findBaseUsersByEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    baseUserRepo.save(user);
    return ResponseEntity.ok(new ResetPassword("password.changed.successfully"));
}
```

**Classes جديدة:**
```java
// ForgotPasswordRequest.java
public class ForgotPasswordRequest {
    private String email;
}

// ResetPasswordRequest.java
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
```

**أضف هذين في `SecurityConfig.java` للـ public endpoints:**
```java
.requestMatchers("/forgot-password", "/auth/forgot-password").permitAll()
.requestMatchers("/reset-password", "/auth/reset-password").permitAll()
```

---

## 🟡 Phase 3 — RESTful Naming & Structure

### ✅ Task 7 — تصحيح أسماء الـ Endpoints

أضف aliases جانب الاسم القديم لتجنب كسر أي شيء:

| حالياً (الباك) | المطلوب (الفرونت) |
|---|---|
| `POST /signup` | `POST /auth/register` |
| `POST /login` | `POST /auth/login` |
| `POST /logout` | `POST /auth/logout` |
| `POST /signupForTeacher` | `POST /auth/register/teacher` |
| `PUT /resetPass` | `PUT /auth/reset-password` |
| `POST /doEnrollment` | `POST /enrollments` |
| `GET /getAllEnrollmentsForAuthStudent` | `GET /enrollments/my` |
| `GET /get/Pages/Courses` | `GET /courses` |
| `GET /getCourseById?id=1` | `GET /courses/{id}` |
| `POST /save/course` | `POST /courses` |

**طريقة التنفيذ الآمنة — الاسم القديم والجديد بيشتغلوا معاً:**
```java
@PostMapping({"/login", "/auth/login"})
public ResponseEntity<AuthResponse> login(...) { ... }

@GetMapping({"/get/Pages/Courses", "/courses"})
public ResponseEntity<Page<CourseDto>> getCourses(...) { ... }

@PostMapping({"/doEnrollment", "/enrollments"})
public ResponseEntity<EnrollResponse> doEnrollments(...) { ... }

@GetMapping({"/getAllEnrollmentsForAuthStudent", "/enrollments/my"})
public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() { ... }
```

---

## 🟡 Phase 4 — Missing Endpoints

### ✅ Task 8 — Progress Endpoints

الفرونت بيحتاج:
- `GET  /progress` — كل كورسات الطالب مع الـ progress
- `GET  /progress/{courseId}` — progress لكورس معين
- `PATCH /enrollments/{id}/progress` — تحديث درس معين

```java
// إنشئ ProgressController.java
@Tag(name = "Progress")
@RestController
@RequestMapping("/progress")
public class ProgressController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public ProgressController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Get all courses progress for authenticated student")
    @GetMapping
    public ResponseEntity<List<ProgressResponse>> getAllProgress() {
        // اجلب enrollments الطالب واحسب progressPercent لكل كورس
        return ResponseEntity.ok(enrollmentService.getProgressForAuthStudent());
    }

    @Operation(summary = "Get progress for specific course")
    @GetMapping("/{courseId}")
    public ResponseEntity<ProgressResponse> getCourseProgress(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseProgress(courseId));
    }
}

// في EnrollmentController أضف:
@PatchMapping("/enrollments/{enrollmentId}/progress")
public ResponseEntity<?> updateLessonProgress(
        @PathVariable Long enrollmentId,
        @RequestBody LessonProgressRequest request) {
    // request: { "lessonId": 3, "completed": true }
    return ResponseEntity.ok(enrollmentService.updateLessonProgress(enrollmentId, request));
}
```

**`ProgressResponse.java`:**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressResponse {
    private Long   courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private int    progressPercent;   // 0-100
    private int    completedLessons;
    private int    totalLessons;
    private String lastAccessedAt;    // ISO-8601
}
```

**`LessonProgressRequest.java`:**
```java
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class LessonProgressRequest {
    private Long    lessonId;
    private boolean completed;
}
```

---

### ✅ Task 9 — Swagger Annotations

```java
// SecurityController.java
@Tag(name = "Authentication", description = "Login, Register, Logout, Password Reset")
@RestController
public class SecurityController {
    @Operation(summary = "Student Login", description = "Returns JWT + user payload")
    @PostMapping({"/login", "/auth/login"})
    public ResponseEntity<AuthResponse> login(...) { ... }

    @Operation(summary = "Student Register")
    @PostMapping({"/signup", "/auth/register"})
    public ResponseEntity<AuthResponse> signup(...) { ... }

    @Operation(summary = "Teacher Register")
    @PostMapping({"/signupForTeacher", "/auth/register/teacher"})
    public ResponseEntity<AuthResponse> signupForTeacher(...) { ... }

    @Operation(summary = "Logout — invalidates token")
    @PostMapping({"/logout", "/auth/logout"})
    public ResponseEntity<LogoutResponse> logout() { ... }
}

// CourseController.java
@Tag(name = "Courses", description = "Browse and manage courses")
@RestController
public class CourseController {
    @Operation(summary = "Get all courses with pagination")
    @GetMapping({"/get/Pages/Courses", "/courses"})
    public ResponseEntity<Page<CourseDto>> getCourses(...) { ... }

    @Operation(summary = "Get course by ID with lectures")
    @GetMapping({"/getCourseById", "/courses/{id}"})
    public ResponseEntity<CourseResponse> getCourseById(...) { ... }

    @Operation(summary = "Create a new course")
    @PostMapping({"/save/course", "/courses"})
    public ResponseEntity<CourseDto> saveCourse(...) { ... }
}

// EnrollmentController.java
@Tag(name = "Enrollments")
@RestController
public class EnrollmentController {
    @Operation(summary = "Enroll in a course")
    @PostMapping({"/doEnrollment", "/enrollments"})
    public ResponseEntity<EnrollResponse> doEnrollments(...) { ... }

    @Operation(summary = "Get all enrollments for authenticated student")
    @GetMapping({"/getAllEnrollmentsForAuthStudent", "/enrollments/my"})
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() { ... }
}
```

---

### ✅ Task 10 — CORS Configuration

**في `SecurityConfig.java` أضف `CorsConfigurationSource` bean:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:5173",           // EDUKO Student dev
        "http://localhost:5174",           // EDUKO Admin dev
        "https://your-production-domain.com"
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

> الـ `cors(Customizer.withDefaults())` الموجودة في `securityFilterChain` هتشتغل تلقائياً مع الـ bean الجديد.

---

## 📂 ملخص الملفات التي تتعدل

```
src/main/java/com/springboot/eduko/
├── controller/
│   ├── SecurityController.java        ← Phase 1 + 2 + 3
│   ├── CourseController.java          ← Phase 2 + 3
│   ├── EnrollmentController.java      ← Phase 1 + 3 + 4
│   └── ProgressController.java        ← Phase 4 (جديد)
├── controller/vms/
│   ├── AuthResponse.java              ← Phase 1 (جديد)
│   ├── EnrollRequest.java             ← Phase 1
│   ├── EnrollResponse.java            ← Phase 1
│   ├── ForgotPasswordRequest.java     ← Phase 2 (جديد)
│   ├── ResetPasswordRequest.java      ← Phase 2 (جديد)
│   ├── ProgressResponse.java          ← Phase 4 (جديد)
│   ├── LessonProgressRequest.java     ← Phase 4 (جديد)
│   └── SignupRequestForStudent.java   ← Phase 1 (remove @NotBlank)
├── dtos/
│   └── CourseDto.java                 ← Phase 2
├── model/
│   └── EduCourses.java                ← Phase 2 (ضيف columns)
├── config/
│   └── SecurityConfig.java            ← Phase 3 (CORS + new endpoints)
└── swagger/
    └── ShowSwagger.java               ← موجود بالفعل
```

---

## 🔁 ترتيب التنفيذ المقترح

```
Day 1 — Phase 1 (Critical)  ≈ 4-5 ساعات
  ├── Task 1: AuthResponse.java + login/signup/signupForTeacher
  ├── Task 2: SignupRequestForStudent optional fields
  ├── Task 3: EnrollRequest → courseId
  └── Task 4: EnrollResponse full object

Day 2 — Phase 2 (High)  ≈ 4 ساعات
  ├── Task 5: CourseDto new fields + EduCourses model + migration
  └── Task 6: Password Reset secure two-step flow

Day 3 — Phase 3 & 4  ≈ 5 ساعات
  ├── Task 7: RESTful endpoint aliases
  ├── Task 8: ProgressController + LessonProgress
  ├── Task 9: Swagger @Tag @Operation
  └── Task 10: CORS config
```

---

## ✅ Acceptance Criteria — كيف تتحقق من كل Task

| Task | معيار النجاح |
|------|-----------|
| Task 1 | `POST /auth/login` → response فيها `user.id` و `user.role` |
| Task 2 | `POST /auth/register` بدون `parentName` → 200 OK |
| Task 3 | `POST /enrollments` بـ `{ "courseId": 1 }` → 200 OK |
| Task 4 | Enroll response فيها `id`, `courseTitle`, `enrolledAt` |
| Task 5 | `GET /courses` → كل course فيه `price`, `thumbnailUrl`, `rating` |
| Task 6 | `POST /auth/forgot-password` → `{ "resetToken": "..." }` |
| Task 7 | `GET /courses` يشتغل بدل `/get/Pages/Courses` |
| Task 8 | `GET /progress` → list بالـ progressPercent |
| Task 9 | `/swagger-ui.html` → كل endpoint معه description و tag |
| Task 10 | Request من `localhost:5173` يوصل بدون CORS error |

---

> **تم إعداد هذه الخطة بناءً على مقارنة كاملة بين:**
> - [EDUKO Frontend](https://github.com/ahmedhussien1pro/EDUKO) — Student Platform
> - [EDUKO ADMIN](https://github.com/ahmedhussien1pro/EDUKO-ADMIN) — Admin Dashboard
> - [Eduko1 Backend](https://github.com/ahmedhabiby/Eduko1) — Spring Boot API (five commit — Apr 30, 2026)
