# EDUKO Backend — Changelog

> All changes made to [`ahmedhabiby/Eduko1`](https://github.com/ahmedhabiby/Eduko1) starting from commit [`f2a4907`](https://github.com/ahmedhabiby/Eduko1/commit/f2a4907d3cf9c489be1f06a7c568f64ed64f75ef)

---

## Summary

| Phase | Description | Commits |
|---|---|---|
| Phase 1 | Auth & Enrollment alignment with Frontend | [1c67c2e](https://github.com/ahmedhabiby/Eduko1/commit/1c67c2e71da26d0a8c7f8b7f67479ae59d2362e5) |
| Phase 2/3/4 | Courses, Progress endpoints, RESTful aliases | [e16ab24](https://github.com/ahmedhabiby/Eduko1/commit/e16ab24721ce024759734469a2944155b33f2c8c) |
| Phase 4b | Real progress tracking via LectureAccess | [263b115](https://github.com/ahmedhabiby/Eduko1/commit/263b1151f70757ae0d781571487d5829ef648a9e) |
| Phase 5a | Admin foundation — models, entities, security | [8403199](https://github.com/ahmedhabiby/Eduko1/commit/840319987a5cc41e96d1aaf26d655c37c11e15fd) |
| Phase 5b-1 | Repos + AuditLogService | [babea67](https://github.com/ahmedhabiby/Eduko1/commit/babea67a52204e76cb6a6a5aa1aabfd0b049adc9) |
| Phase 5b-2 | Admin Auth + Users + Courses controllers | [701688e](https://github.com/ahmedhabiby/Eduko1/commit/701688ee2b45bc8e8717f6d88f7b243181908803) |
| Phase 5b-3 | Admin Enrollment + Payment + Report + Audit | [9b49215](https://github.com/ahmedhabiby/Eduko1/commit/9b49215c19409340b40b9ee68dbe1a3b42c99b76) |
| Fixes | Auth aliases, Admin user create, progress patch | [009e977](https://github.com/ahmedhabiby/Eduko1/commit/009e97718b4380c8fc4c1821b8950166ec69741c) · [1fa2ebb](https://github.com/ahmedhabiby/Eduko1/commit/1fa2ebb216678e56213f8b4594cedf341dd514f0) · [4c934ec](https://github.com/ahmedhabiby/Eduko1/commit/4c934ec1a4041247bfcf1fc9a00ca301709fcded) · [9cb367a](https://github.com/ahmedhabiby/Eduko1/commit/9cb367ad8aa16eb7affad4dfa4318f85858bd093) |
| Blockers Fix | Favorite entity + Repo + CourseController defaults | [032ded8](https://github.com/ahmedhabiby/Eduko1/commit/032ded8d19bee512101d0ae9840bd93b42076333) · [c3a9ba0](https://github.com/ahmedhabiby/Eduko1/commit/c3a9ba03a630999689cb01d5dad3193325692673) |
| Swagger | OpenAPI v2 — servers, JWT auth, tags | [8538d07](https://github.com/ahmedhabiby/Eduko1/commit/8538d07213551d2f0e71f689e8c8c7c4f45e0a44) |

---

## Phase 1 — Auth & Enrollment Alignment

> Align authentication and enrollment responses with the EDUKO Frontend API contracts.

**Commit:** [1c67c2e](https://github.com/ahmedhabiby/Eduko1/commit/1c67c2e71da26d0a8c7f8b7f67479ae59d2362e5)

| File | Type | Reason |
|---|---|---|
| `controller/vms/AuthResponse.java` |  New | Response shape `{ token, user: { id, name, email, role, avatar } }` required by Frontend |
| `service/AuthService.java` |  Updated | Changed return type to `AuthResponse` |
| `service/impl/AuthServiceImpl.java` |  Updated | Build and return `AuthResponse` on login/register |
| `controller/SecurityController.java` |  Updated | Added RESTful aliases: `/auth/login`, `/auth/register`, `/auth/logout`, `/auth/forgot-password`, `/auth/reset-password` |
| `config/SecurityConfig.java` | Updated | Added CORS config + permit `/auth/**` and `/admin/auth/**` without JWT |
| `controller/vms/SignupRequestForStudent.java` | Updated | Made `parentName`, `parentNumber`, `studentNumber` optional |
| `controller/vms/EnrollRequest.java` |  Updated | Changed body from `{ studentEmail, courseTitle }` → `{ courseId }` |
| `controller/vms/EnrollResponse.java` |  Updated | Expanded to `{ id, courseId, courseTitle, enrolledAt, status }` |
| `controller/vms/ForgotPasswordRequest.java` |  New | Request VM for `POST /auth/forgot-password` |
| `controller/vms/ResetPasswordRequest.java` |  New | Request VM for `PUT /auth/reset-password` |

---

## Phase 2/3/4 — Courses, Progress & RESTful Endpoints

> Add missing course fields, fix enrollment logic, add progress tracking endpoints.

**Commit:** [e16ab24](https://github.com/ahmedhabiby/Eduko1/commit/e16ab24721ce024759734469a2944155b33f2c8c)

| File | Type | Reason |
|---|---|---|
| `model/EduCourses.java` |  Updated | Added: `description`, `thumbnailUrl`, `price`, `currency`, `level`, `category`, `instructorName`, `rating`, `enrolledCount`, `status` |
| `model/Enrollments.java` |  Updated | Added `status` field |
| `dtos/CourseDto.java` | Updated | Added all new course fields |
| `controller/vms/CourseResponse.java` | Updated | Added `id` + all new fields |
| `service/impl/EnrollmentServiceImpl.java` | Updated | Fixed `doEnrollments` to use `courseId` + JWT auth instead of `studentEmail/courseTitle` |
| `controller/EnrollmentController.java` | Updated | Added dual-path mapping + `@Tag/@Operation` + `PATCH /enrollments/{id}/progress` |
| `controller/CourseController.java` | Updated | Added dual-path `/courses` alias + `GET /courses/{id}` + Swagger annotations |
| `controller/ProgressController.java` | New | New controller: `GET /progress` + `GET /progress/{courseId}` |
| `controller/vms/ProgressResponse.java` | New | Response shape for progress endpoints |
| `controller/vms/LessonProgressRequest.java` | New | Request body for lesson progress update |

---

## Phase 4b — Real Progress Tracking

> Replace TODO placeholders with real DB-backed progress calculation via `LectureAccess`.

**Commit:** [263b115](https://github.com/ahmedhabiby/Eduko1/commit/263b1151f70757ae0d781571487d5829ef648a9e)

| File | Type | Reason |
|---|---|---|
| `repo/LectureAccessRepo.java` | Updated | Added queries: `findByStudentIdAndLecturesId`, `countByStudentId...AndStatus`, `findByStudentIdAndLecturesEduCoursesId` |
| `service/impl/EnrollmentServiceImpl.java` | Updated | Wired `LectureAccessRepo` + `LectureRepo` — real `progressPercent` from DB, removed all TODO placeholders |

---

## Phase 5a — Admin Foundation

> Lay the groundwork for the Admin Panel: new entities, updated models, security rules.

**Commit:** [8403199](https://github.com/ahmedhabiby/Eduko1/commit/840319987a5cc41e96d1aaf26d655c37c11e15fd)

| File | Type | Reason |
|---|---|---|
| `model/BaseUser.java` | Updated | Added `status` field (`active` / `inactive` / `banned`) |
| `model/EduCourses.java` | Updated | Added `titleAr`, `descriptionAr`, `requiresApproval` |
| `model/Lectures.java` | Updated | Added `titleAr`, `order`, `type`, `driveFileId`, `startTimestamp`, `endTimestamp`, `durationMin`, `isFreePreview` |
| `model/Enrollments.java` | Updated | Added `accessLevel`, `paymentStatus`, `paymentProofUrl`, `paymentAmount`, `paymentApprovedBy/At`, `accessGranted/ExpiresAt`, `completedAt` |
| `model/CourseModule.java` | New | Entity for Course → Module → Lesson hierarchy |
| `model/AuditLog.java` | New | Entity to record every Admin action |
| `model/PaymentProof.java` | New | Entity for manual payment proof submission and review |
| `repo/CourseModuleRepo.java` | New | JPA Repository for `CourseModule` |
| `repo/AuditLogRepo.java` | New | JPA Repository for `AuditLog` |
| `repo/PaymentProofRepo.java` | New | JPA Repository for `PaymentProof` |
| `config/SecurityConfig.java` | Updated | Added `/admin/**` secured block + `/admin/auth/**` public |

---

## Phase 5b-1 — Repos & AuditLogService

> Add missing repository methods and implement the AuditLog service.

**Commit:** [babea67](https://github.com/ahmedhabiby/Eduko1/commit/babea67a52204e76cb6a6a5aa1aabfd0b049adc9)

| File | Type | Reason |
|---|---|---|
| `repo/EnrollmentRepo.java` | Updated | Added `findByEduCoursesId`, `countByStudentId`, `findByStudentIdAndEduCoursesId` |
| `repo/LectureRepo.java` | Updated | Added `findByModuleId`, `findByEduCoursesIdOrderByOrder` |
| `repo/CourseModuleRepo.java` | Updated | Fixed method name typo (`courseid` → `courseId`) |
| `service/AuditLogService.java` | New | Interface: `log(actor, action, target)` |
| `service/impl/AuditLogServiceImpl.java` | New | Implementation — auto-saves to `AuditLog` entity on every admin action |

---

## Phase 5b-2 — Admin Auth, Users & Courses Controllers

> Full CRUD controllers for the Admin Panel — authentication, user management, course management.

**Commit:** [701688e](https://github.com/ahmedhabiby/Eduko1/commit/701688ee2b45bc8e8717f6d88f7b243181908803)

| File | Type | Reason |
|---|---|---|
| `controller/admin/AdminAuthController.java` | New | `POST /admin/auth/login` (validates ADMIN role + returns JWT) · `POST /admin/auth/logout` (blacklists token) |
| `controller/admin/AdminUserController.java` | New | Full CRUD: `/admin/users`, `/admin/students`, `/admin/teachers`, reset-password, status management |
| `controller/admin/AdminCourseController.java` | New | Full CRUD: `/admin/courses`, `/admin/courses/{id}/modules`, `/admin/courses/{id}/modules/{mid}/lessons` |

---

## Phase 5b-3 — Admin Enrollment, Payment, Report & Audit

> Complete the Admin Panel backend — enrollments, payments, analytics, and audit logging.

**Commit:** [9b49215](https://github.com/ahmedhabiby/Eduko1/commit/9b49215c19409340b40b9ee68dbe1a3b42c99b76)

| File | Type | Reason |
|---|---|---|
| `controller/admin/AdminEnrollmentController.java` | New | CRUD enrollments + `approve` / `reject` + access control endpoints |
| `controller/admin/AdminPaymentController.java` | New | Review and approve/reject payment proofs · `/admin/payments` + `/admin/proofs` |
| `controller/admin/AdminReportController.java` | New | `GET /admin/reports` — real platform statistics from DB (users, courses, enrollments) |
| `controller/admin/AdminAuditController.java` | New | `GET /admin/audit-logs` — full paginated log of all admin actions |

---

## Fixes & Patches

> Small targeted fixes to align specific endpoints with what the Frontend expects.

| File | Commit | Type | Reason |
|---|---|---|---|
| `controller/SecurityController.java` | [009e977](https://github.com/ahmedhabiby/Eduko1/commit/009e97718b4380c8fc4c1821b8950166ec69741c) | Updated | Added `/auth/login` + `/auth/logout` aliases needed by the Admin Frontend |
| `controller/admin/AdminUserController.java` | [1fa2ebb](https://github.com/ahmedhabiby/Eduko1/commit/1fa2ebb216678e56213f8b4594cedf341dd514f0) | Updated | Added `POST /admin/users` to create student or teacher directly from Admin Panel |
| `controller/admin/AdminEnrollmentController.java` | [4c934ec](https://github.com/ahmedhabiby/Eduko1/commit/4c934ec1a4041247bfcf1fc9a00ca301709fcded) | Updated | Added `PATCH /admin/enrollments/{id}/progress` |
| `controller/admin/AdminPaymentController.java` | [9cb367a](https://github.com/ahmedhabiby/Eduko1/commit/9cb367ad8aa16eb7affad4dfa4318f85858bd093) | Updated | Added `/admin/payment-proofs` alias to match Frontend API path |

---

## Blockers Fix

> Critical fixes that were blocking the app from compiling and running correctly.

| File | Commit | Type | Reason |
|---|---|---|---|
| `model/Favorite.java` | [032ded8](https://github.com/ahmedhabiby/Eduko1/commit/032ded8d19bee512101d0ae9840bd93b42076333) | New | Entity was missing — `FavoriteController` referenced it, causing compile failure |
| `repo/FavoriteRepo.java` | [032ded8](https://github.com/ahmedhabiby/Eduko1/commit/032ded8d19bee512101d0ae9840bd93b42076333) | New | JPA Repo with `findByUserId`, `findByUserIdAndCourseId`, `deleteByUserIdAndCourseId` |
| `controller/FavoriteController.java` | [c3a9ba0](https://github.com/ahmedhabiby/Eduko1/commit/c3a9ba03a630999689cb01d5dad3193325692673) | Updated | Use `FavoriteRepo.deleteByUserIdAndCourseId` + enriched course fields in response |
| `controller/CourseController.java` | [c3a9ba0](https://github.com/ahmedhabiby/Eduko1/commit/c3a9ba03a630999689cb01d5dad3193325692673) | Updated | Added `defaultValue="0"` and `defaultValue="20"` to `page`/`size` — fixed 400 error when called without params |

---

## Swagger / OpenAPI Update

> Upgrade OpenAPI config from a bare minimum stub to a production-ready documentation setup.

**Commit:** [8538d07](https://github.com/ahmedhabiby/Eduko1/commit/8538d07213551d2f0e71f689e8c8c7c4f45e0a44)

| File | Type | Reason |
|---|---|---|
| `swagger/ShowSwagger.java` | Updated | Added: `@Server` (localhost + production URL) · `@SecurityScheme` BearerAuth JWT · global `@SecurityRequirement` · 13 ordered `@Tag` entries · version bumped to `v2.0` |

---

## Endpoints Reference

### Student API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register new student |
| `POST` | `/auth/login` | Login — returns `{ token, user }` |
| `POST` | `/auth/logout` | Logout — invalidates token |
| `POST` | `/auth/forgot-password` | Request password reset email |
| `PUT` | `/auth/reset-password` | Reset password with token |
| `GET` | `/users/me` | Get authenticated student profile |
| `PATCH` | `/users/me` | Update profile |
| `PATCH` | `/users/me/password` | Change password |
| `GET` | `/courses` | List all courses (paginated, default page=0 size=20) |
| `GET` | `/courses/{id}` | Get course details with lectures |
| `GET` | `/enrollments` | List student enrollments |
| `POST` | `/enrollments` | Enroll in a course `{ courseId }` |
| `PATCH` | `/enrollments/{id}/progress` | Update lesson progress |
| `GET` | `/progress` | Get all course progress |
| `GET` | `/progress/{courseId}` | Get progress for specific course |
| `GET` | `/favorites` | List favorite courses |
| `POST` | `/favorites` | Add course to favorites `{ courseId }` |
| `DELETE` | `/favorites/{courseId}` | Remove course from favorites |

### Admin API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/auth/login` | Admin login — validates `ROLE_ADMIN` |
| `POST` | `/admin/auth/logout` | Admin logout |
| `GET` | `/admin/users` | List all users |
| `POST` | `/admin/users` | Create student or teacher |
| `GET/PATCH/DELETE` | `/admin/users/{id}` | Get, update, or delete user |
| `PATCH` | `/admin/users/{id}/status` | Change user status |
| `POST` | `/admin/users/{id}/reset-password` | Reset user password |
| `GET` | `/admin/students` | List all students |
| `GET` | `/admin/teachers` | List all teachers |
| `GET/POST` | `/admin/courses` | List or create courses |
| `GET/PATCH/DELETE` | `/admin/courses/{id}` | Get, update, or delete course |
| `PATCH` | `/admin/courses/{id}/status` | Change course status |
| `GET/POST` | `/admin/courses/{id}/modules` | List or create modules |
| `PATCH/DELETE` | `/admin/courses/{id}/modules/{mid}` | Update or delete module |
| `GET/POST` | `/admin/courses/{id}/modules/{mid}/lessons` | List or create lessons |
| `PATCH/DELETE` | `/admin/courses/{id}/modules/{mid}/lessons/{lid}` | Update or delete lesson |
| `GET/POST` | `/admin/enrollments` | List or create enrollments |
| `POST` | `/admin/enrollments/{id}/approve` | Approve enrollment |
| `POST` | `/admin/enrollments/{id}/reject` | Reject enrollment |
| `GET` | `/admin/payments` | List all payments |
| `GET` | `/admin/payment-proofs` | List payment proofs |
| `POST` | `/admin/payments/approvals/{id}/approve` | Approve payment |
| `POST` | `/admin/payments/approvals/{id}/reject` | Reject payment |
| `GET` | `/admin/reports` | Platform statistics from DB |
| `GET` | `/admin/audit-logs` | Admin action audit log |

---

> **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`  
> **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
