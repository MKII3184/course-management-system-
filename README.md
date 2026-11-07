# Course Management System (Spring Boot Enterprise REST API)

![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![MinIO](https://img.shields.io/badge/MinIO-Object%20Storage-red.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-green.svg)
![Tests](https://img.shields.io/badge/Unit%20Tests-222%20Passed-success.svg)

Hệ thống quản lý khóa học (Course Management System) là ứng dụng Backend chuẩn Enterprise được phát triển bằng **Spring Boot** và **Java 21 LTS**, cung cấp hệ thống **RESTful API** toàn diện cho Khóa học (Courses), Học viên (Students) và Tài liệu tài nguyên (Documents).

Dự án áp dụng mô hình kiến trúc phân tầng chuẩn mực, kết hợp **Hibernate Generic DAO Hierarchy 3 tầng**, bộ lọc ngầm **Hibernate Session Filters** (`@FilterDef`, `@Filter`), tìm kiếm động **FIQL & JPA Specification**, mã hóa dữ liệu nhạy cảm **AES-256 (CBC/GCM + PBKDF2)**, lưu trữ nhị phân trên **MinIO Object Storage**, phân quyền đa tầng **Spring Security 6 RBAC** và chuẩn hóa phản hồi **Map API** chống phá vỡ giao diện.

---

## 📌 Mục lục
- [Tính năng nổi bật](#-tính-năng-nổi-bật)
- [Kiến trúc và Công nghệ](#-kiến-trúc-và-công-nghệ)
- [Cấu trúc thư mục dự án](#-cấu-trúc-thư-mục-dự-án)
- [Bảo mật, Phân quyền & Mã hóa](#-bảo-mật-phân-quyền--mã-hóa)
- [Cài đặt & Khởi chạy](#-cài-đặt--khởi-chạy)
- [Tài liệu REST API & Chuẩn Map API](#-tài-liệu-rest-api--chuẩn-map-api)
- [Swagger UI & OpenAPI 3.0](#-swagger-ui--openapi-30)
- [Kiểm thử tự động](#-kiểm-thử-tự-động)
- [Tác giả](#-tác-giả)

---

## 🚀 Tính năng nổi bật

### 1. Quản lý Khóa học (Course Management)
- CRUD toàn diện qua mô hình Generic DAO với kiểm tra dữ liệu phòng thủ (`findByPk`, `ValidationException`).
- Tìm kiếm động nâng cao theo tiêu đề (Case-insensitive JPQL), lọc theo khoảng học phí và phân trang an toàn.
- Chuẩn hóa API xóa và cập nhật linh hoạt qua JSON Map (`POST /delete`, `POST /update`), thống kê số lượng (`GET /count`).

### 2. Quản lý Học viên (Student Management)
- Tự động mã hóa hai chiều các thông tin nhạy cảm (Số điện thoại, địa chỉ, CMND/CCCD) bằng **AES-256** trước khi ghi xuống MySQL và tự động giải mã khi đọc lên qua JPA `AttributeConverter`.
- Tìm kiếm học viên theo từ khóa (họ tên, email) và phân trang sắp xếp nhiều tiêu chí.

### 3. Quản lý Tài liệu & MinIO Object Storage (Document Management)
- Tách biệt dữ liệu nhị phân (lưu trên **MinIO**) và metadata (lưu trên MySQL).
- **Mã hóa nhị phân**: Tự động mã hóa luồng byte file bằng AES-256 với IV ngẫu nhiên trước khi upload lên MinIO và giải mã khi download.
- **Tự động tăng phiên bản (Versioning)**: Khi upload file trùng tên trong cùng `appId`, hệ thống tự động tăng `version = version + 1.0` và deactivate các phiên bản cũ.
- **Xóa mềm (Soft Delete) & Khôi phục (Restore)**: Đánh dấu `deleted = true, active = false` và cung cấp API khôi phục tài liệu an toàn.
- **Bảo vệ chống Path Traversal**: Tự động lọc sạch tên file độc hại (`FileUtil.sanitizeFileName`).

### 4. Thiết kế Kiến trúc Enterprise Nâng cao
- **Generic DAO 3 tầng**: `IGenericDao` ➔ `IPaginationSortingDao` ➔ `[EntityDao]`. Triển khai kế thừa: `HibernateGenericDao` ➔ `HibernatePaginationSortingDao` ➔ `[EntityDaoImpl]`.
- **Hibernate Session Filters**: Sử dụng `session.enableFilter(...)` kết hợp `@FilterDef` trên Entity để tự động lọc dữ liệu theo `appId`, `userAppId`, `deleted` mà không cần viết điều kiện lặp lại trong SQL.
- **Lập trình phòng vệ (Defensive Service)**: 100% phương thức Service ghi log đầu vào (`logger.info`), kiểm tra ID null, bắt buộc kiểm tra tồn tại qua `findByPk`, phân nhánh trạng thái và trả về `Map.of(STATUS, SUCCESS)`.
- **Triệt tiêu Hardcode (Zero Hardcode)**: 100% thông báo lỗi, thông báo thành công và Map keys tập trung tại `MessageConstants.java`, URL REST tại `ApiConstants.java`, Role tại `RoleConstants.java`.

---

## 🛠 Kiến trúc và Công nghệ

- **Ngôn ngữ**: Java 21 LTS
- **Framework**: Spring Boot 3.x (Spring Web MVC, Spring Data JPA, Spring Security 6)
- **ORM / Persistence**: Hibernate 7.x, Jakarta Persistence (JPA)
- **Database**: MySQL 8.x
- **Object Storage**: MinIO Object Storage
- **Security & Encryption**: Spring Security 6 Component-Based, BCrypt, AES-256 (CBC/GCM, PBKDF2WithHmacSHA256, SecureRandom IV)
- **API Documentation**: Springdoc OpenAPI 3.0 / Swagger UI
- **Build Tool**: Apache Maven (kèm `mvnw` & `mvnw.cmd`)
- **Testing**: JUnit 5, Mockito, MockMvc, JaCoCo Code Coverage (222 Unit Tests pass 100%)

---

## 📂 Cấu trúc thư mục dự án

```text
course-management-system/
├── pom.xml                                         # Cấu hình dependencies Maven
├── mvnw / mvnw.cmd                                 # Maven Wrapper đa nền tảng
├── README.md                                       # Tài liệu hướng dẫn dự án
└── src/
    ├── main/
    │   ├── java/com/mkii/coursemanagementsystem/
    │   │   ├── CourseManagementSystemApplication.java # Khởi chạy ứng dụng Spring Boot
    │   │   ├── common/                             # Định nghĩa hằng số & ngoại lệ tập trung
    │   │   │   ├── ApiConstants.java               # URL Endpoints & Query Params
    │   │   │   ├── MessageConstants.java           # 100% Chuỗi thông báo hệ thống
    │   │   │   ├── RoleConstants.java              # Hằng số vai trò & biểu thức @PreAuthorize
    │   │   │   ├── converter/                      # JPA Attribute Converters
    │   │   │   │   └── AesEncryptConverter.java    # Tự động mã hóa AES Entity columns
    │   │   │   └── exception/                      # Quản lý ngoại lệ tập trung
    │   │   │       ├── BadRequestException.java
    │   │   │       ├── ErrorResponse.java
    │   │   │       ├── GlobalExceptionHandler.java
    │   │   │       ├── ResourceNotFoundException.java
    │   │   │       └── ValidationException.java
    │   │   ├── config/                             # Cấu hình Beans hệ thống
    │   │   │   ├── MinioConfig.java                # Cấu hình MinIO Client
    │   │   │   ├── OpenApiConfig.java              # Cấu hình Swagger / OpenAPI 3.0
    │   │   │   └── SecurityConfig.java             # Cấu hình Spring Security 6 (RBAC, CORS, Stateless)
    │   │   ├── dao/                                # Data Access Object interfaces
    │   │   │   ├── IGenericDao.java                # Generic DAO Interface gốc
    │   │   │   ├── IPaginationSortingDao.java      # Generic DAO Phân trang, Sắp xếp & Search
    │   │   │   ├── CourseDao.java
    │   │   │   ├── StudentDao.java
    │   │   │   ├── DocumentDao.java
    │   │   │   └── impl/                           # Triển khai DAO với Hibernate Session
    │   │   │       ├── CourseDaoImpl.java
    │   │   │       ├── StudentDaoImpl.java
    │   │   │       └── DocumentDaoImpl.java
    │   │   ├── dto/                                # Data Transfer Objects
    │   │   │   ├── CourseRequestDTO.java / CourseResponseDTO.java
    │   │   │   ├── StudentRequestDTO.java / StudentResponseDTO.java
    │   │   │   └── DocumentRequestDTO.java / DocumentResponseDTO.java
    │   │   ├── entity/                             # JPA Entities ánh xạ database
    │   │   │   ├── Course.java
    │   │   │   ├── Student.java
    │   │   │   └── Document.java                   # Hỗ trợ @FilterDef & @Filter Hibernate
    │   │   ├── rest/                               # REST Controller Interfaces & Annotations
    │   │   │   ├── CourseRest.java
    │   │   │   ├── StudentRest.java
    │   │   │   ├── DocumentRest.java
    │   │   │   ├── SecurityRest.java
    │   │   │   └── impl/                           # Triển khai REST Controller
    │   │   │       ├── CourseRestImpl.java
    │   │   │       ├── StudentRestImpl.java
    │   │   │       ├── DocumentRestImpl.java
    │   │   │       └── SecurityRestImpl.java
    │   │   ├── service/                            # Tầng Business Logic & Phòng vệ
    │   │   │   ├── CourseService.java / CourseServiceImpl.java
    │   │   │   ├── StudentService.java / StudentServiceImpl.java
    │   │   │   ├── DocumentService.java / DocumentServiceImpl.java
    │   │   │   └── MinioStorageService.java        # Xử lý upload/download MinIO mã hóa
    │   │   ├── specification/                      # Bộ phân tích cú pháp tìm kiếm JPA Spec
    │   │   │   ├── GenericSpecification.java
    │   │   │   └── GenericSpecificationsBuilder.java
    │   │   └── util/                               # Lớp tiện ích kỹ thuật thuần túy
    │   │       ├── AESUtil.java                    # Mã hóa AES (CBC, GCM, PBKDF2, IV, Stream)
    │   │       ├── DateTimeUtil.java               # Định dạng ngày giờ, Epoch milliseconds
    │   │       ├── FileUtil.java                   # Sanitize tên file, check định dạng, byte size
    │   │       ├── SecurityUtil.java               # Trích xuất Current User từ SecurityContext
    │   │       └── StringUtil.java                 # Che mờ dữ liệu nhạy cảm, URL slug
    │   └── resources/
    │       └── application.properties              # Cấu hình MySQL, MinIO, Logging, Security
    └── test/                                       # 222 Unit Tests & MockMvc Slices
```

---

## 🔒 Bảo mật, Phân quyền & Mã hóa

### 1. Phân quyền vai trò (Role-Based Access Control)
Hệ thống sử dụng **Spring Security 6** với kiến trúc Stateless REST và kiểm soát truy cập cấp phương thức `@PreAuthorize`:
- **`ADMIN`**: Toàn quyền quản trị (Thêm, sửa, xóa khóa học, học viên, tài liệu).
- **`LECTURER`**: Quản lý khóa học, upload tài liệu, xem danh sách học viên.
- **`STUDENT`**: Quyền đọc và xem tài nguyên học tập.

### 2. Tiêu chuẩn mã hóa
- **Mật khẩu**: Băm bằng `BCryptPasswordEncoder`.
- **Dữ liệu nhạy cảm (Entity)**: Cột số điện thoại, địa chỉ của học viên được mã hóa bằng **AES-256** tự động thông qua `AesEncryptConverter`.
- **Tập tin MinIO**: Mọi tệp nhị phân được mã hóa AES kèm IV ngẫu nhiên trước khi lưu lên storage server.

---

## ⚙️ Cài đặt & Khởi chạy

### 1. Yêu cầu môi trường
- **JDK 21 LTS** trở lên.
- **MySQL 8.0+** (port `3306`).
- **MinIO Server** (tùy chọn cho upload file, port `9000`).

### 2. Cấu hình `application.properties`
```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/course_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=dmcvmkii28
spring.jpa.hibernate.ddl-auto=update

# MinIO Configuration
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=documents
```

### 3. Biên dịch và Chạy
```powershell
# Biên dịch kiểm tra mã nguồn (yêu cầu 0 lỗi)
.\mvnw.cmd clean test-compile

# Chạy toàn bộ 222 Unit Tests
.\mvnw.cmd test

# Khởi động ứng dụng
.\mvnw.cmd spring-boot:run
```
Ứng dụng sẽ hoạt động tại: `http://localhost:8080`

---

## 📡 Tài liệu REST API & Chuẩn Map API

### 1. Bảng Tra Cứu Hợp Đồng Map API Cho Frontend

| Chức năng | Method | Endpoint | Request Body mẫu | Response Body mẫu |
| :--- | :---: | :--- | :--- | :--- |
| **Xóa Khóa học (Map)** | `POST` | `/api/v1/courses/delete` | `{"id": "1"}` | `{"status": "SUCCESS", "message": "Course deleted successfully"}` |
| **Xóa Khóa học (Legacy)** | `DELETE`| `/api/v1/courses/{id}` | *(Trống)* | `{"status": "SUCCESS", "message": "Course deleted successfully"}` |
| **Cập nhật Khóa học (Map)** | `POST` | `/api/v1/courses/update` | `{"id": 1, "title": "New"}` | `{"status": "SUCCESS", "message": "Course updated successfully"}` |
| **Đếm Khóa học (Map)** | `GET` | `/api/v1/courses/count` | *(Trống)* | `{"status": "SUCCESS", "count": 25}` |
| **Xóa Học viên (Map)** | `POST` | `/api/v1/students/delete` | `{"id": "1"}` | `{"status": "SUCCESS", "message": "Student deleted successfully"}` |
| **Cập nhật Học viên (Map)**| `POST` | `/api/v1/students/update` | `{"id": 1, "email": "a@b.c"}`| `{"status": "SUCCESS", "message": "Student updated successfully"}`|
| **Đếm Học viên (Map)** | `GET` | `/api/v1/students/count` | *(Trống)* | `{"status": "SUCCESS", "count": 50}` |
| **Đếm Tài liệu (Map)** | `GET` | `/api/v1/documents/count`| `?appId=APP01` | `{"status": "SUCCESS", "count": 42}` |
| **Khôi phục Tài liệu (Map)**| `POST`| `/api/v1/documents/restore`| `{"id": "1"}` | `{"status": "SUCCESS", "message": "Document restored successfully"}`|

### 2. Ví dụ cURL thao tác Map API:

```bash
# 1. Đếm số lượng khóa học (Nhận JSON Map)
curl -u admin:admin123 http://localhost:8080/api/v1/courses/count

# 2. Xóa khóa học bằng JSON Map Body (Bảo mật ẩn ID)
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/courses/delete \
  -H "Content-Type: application/json" \
  -d '{"id": "1"}'

# 3. Cập nhật linh hoạt thông tin học viên qua Map Body
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/students/update \
  -H "Content-Type: application/json" \
  -d '{"id": 2, "phone": "0988888888", "address": "TP. Ho Chi Minh"}'
```

---

## 📖 Swagger UI & OpenAPI 3.0

Hệ thống tích hợp sẵn Swagger UI tương tác trực tiếp. Người dùng và Frontend developer có thể kiểm thử toàn bộ API trên trình duyệt:

- **Giao diện Swagger UI**: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI 3.0 Spec**: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

> **Hướng dẫn đăng nhập Swagger**: Nhấn nút **Authorize (ổ khóa xanh)** ở góc trên bên phải màn hình Swagger và nhập tài khoản:
> - **Admin**: `admin` / `admin123`
> - **Lecturer**: `lecturer` / `lecturer123`
> - **Student**: `student` / `student123`

---

## 🧪 Kiểm thử tự động (Unit & Integration Tests)

Bộ kiểm thử đạt chuẩn bao phủ toàn diện:
- **Tổng số test cases**: **222 Tests**.
- **Tỷ lệ vượt qua**: **100% Pass** (Failures: 0, Errors: 0, Skipped: 0).
- Bao phủ: REST Controllers (MockMvc slice test), Services (Mockito unit test), Generic DAOs, JPA Specifications, AES encryption & converters, Utilities.

---

## 👤 Tác giả
- **Tác giả**: [MKII3184](https://github.com/MKII3184)
- **Repository**: [course-management-system-](https://github.com/MKII3184/course-management-system-)
