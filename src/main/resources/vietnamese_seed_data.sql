-- ====================================================================
-- Script khởi tạo dữ liệu mẫu Thuần Việt (Vietnamese Seed Data)
-- Hệ thống Quản lý Khóa học & Sinh viên (Course Management System)
-- ====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Xóa sạch dữ liệu test cũ
TRUNCATE TABLE `courses`;
TRUNCATE TABLE `students`;
TRUNCATE TABLE `documents`;

SET FOREIGN_KEY_CHECKS = 1;

-- 2. Thêm danh sách Khóa học Thuần Việt (courses)
INSERT INTO `courses` (`id`, `title`, `description`, `duration`, `price`) VALUES
(1, 'Lập trình Java và Spring Boot Chuyên Sâu', 'Khóa học từ cơ bản đến nâng cao về REST API, Hibernate JPA, Spring Security và Spring Boot Enterprise', 45, 4500000.00),
(2, 'Cấu trúc Dữ liệu & Giải thuật Nâng cao', 'Trang bị nền tảng giải thuật, độ phức tạp tính toán, đồ thị, quy hoạch động cho phỏng vấn kỹ thuật', 60, 3200000.00),
(3, 'Thiết kế & Tối ưu hóa Cơ sở Dữ liệu MySQL', 'Tối ưu hóa truy vấn SQL, chỉ mục (Indexing), giao dịch ACID, phân vùng dữ liệu và kiến trúc CSDL', 36, 2800000.00),
(4, 'Phát triển Web Frontend Hiện đại với React & TypeScript', 'Xây dựng ứng dụng Single Page Application (SPA), quản lý trạng thái Redux/Zustand và tích hợp REST API', 40, 3500000.00),
(5, 'Kiến trúc Vi dịch vụ (Microservices) với Spring Cloud & Docker', 'Thiết kế hệ thống phân tán, API Gateway, Service Discovery, Config Server, Circuit Breaker và Container', 50, 5000000.00),
(6, 'Trí tuệ Nhân tạo & Học máy Ứng dụng', 'Nhập môn Machine Learning, Deep Learning, mạng nơ-ron tích chập (CNN) và xử lý ngôn ngữ tự nhiên (NLP)', 48, 4200000.00),
(7, 'An toàn Thông tin và Kiểm thử Xâm nhập', 'Kỹ thuật bảo mật ứng dụng web OWASP Top 10, mật mã học, mã hóa dữ liệu AES/RSA và phòng chống tấn công', 40, 3800000.00),
(8, 'Quản trị Hệ thống DevOps & CI/CD Thực chiến', 'Tự động hóa quy trình build, test, deploy với GitHub Actions, Docker, Kubernetes và giám sát Prometheus', 35, 3000000.00),
(9, 'Phát triển Ứng dụng Di động Đa nền tảng với Flutter', 'Lập trình ứng dụng di động iOS và Android từ một codebase duy nhất với ngôn ngữ Dart', 45, 2500000.00),
(10, 'Phân tích Dữ liệu Kinh doanh với SQL & Power BI', 'Xử lý, làm sạch và trực quan hóa dữ liệu kinh doanh, xây dựng báo cáo Dashboard phân tích số liệu', 30, 2200000.00),
(11, 'Quản lý Dự án Phần mềm theo chuẩn Agile & Scrum', 'Quy trình phát triển phần mềm linh hoạt, quản lý backlog, sprint planning, retrospective trong nhóm kỹ sư', 25, 1800000.00),
(12, 'Kiểm thử Phần mềm Tự động (Automation Testing)', 'Kiểm thử tự động UI với Selenium/Playwright và API Testing với JUnit, Mockito, Postman', 35, 2400000.00);

-- 3. Thêm danh sách Sinh viên Thuần Việt (students)
-- Ghi chú: phone và address được mã hóa chuẩn AES/CBC/PKCS5Padding tương thích với AesEncryptConverter
INSERT INTO `students` (`id`, `full_name`, `email`, `phone`, `address`) VALUES
(1, 'Nguyễn Văn An', 'nguyenvanan@vju.vnu.edu.vn', '7Kg9O1kheh/7KAqvBCUhfDF2C6DBc0qR9ZL4HKe4izY=', 'MyHtFiFZpVE2a6DOxCov+fdQnPIysTxKw4bqAYu8RcMy/E1y6Q9Kqh3M65Kl6lQ31h24RvXl62Y98e0WEwOpSkh4mdibEr4d7baKEipY397kRKKL47V2F8SqJRFpGYAN'),
(2, 'Trần Thị Mai Hoa', 'tranmaihoa@vju.vnu.edu.vn', '6LnhGvr6ylRGw2OUfAl6qqh7gUpakJJRwufmIYV9jts=', '5UQz8MatOPxXgFcwt2V/bBOwDH+BneP0iUtZU09oYLFL5us2UQqV3a/sAMQwZK8d01fIECWOU5K1sKu45PdX2vy3xWMZCpasrTMscuVo5vlYXfuiPORx9JzCx3YL1/Qp'),
(3, 'Lê Hoàng Long', 'lehoanglong@vju.vnu.edu.vn', 'bN0uXhvCvwmG6nSXsLOwFvgMl7meiFGIVrQX8ozobRg=', 'x8RKYb3PkQkI0may+PGc8btM9df5Pyb131EU3LYvkUsgyw6PusoxwZjvdcvovNiC+S3SjJrsyYDNK617tLiQQye8G/y5ikstwKmy5yTzU9jljeC3G0fvOjAAF7IFe1Xg'),
(4, 'Phạm Minh Tuấn', 'phamminhtuan@vju.vnu.edu.vn', 'NTFV3dZc7lmAKH1NSjo9y+5OtOB4xcXQIGGwjX56e2Q=', 'IPTs25/qdcP9V3mgQqJi+ED+pequcb7w9erbOenYnupUOe0IqZER/KNABuGmvq/s6zLz4ZuOjMqBA81RUc+3EPFGZ+FaEZIq7l1QrhW4ojI='),
(5, 'Đỗ Thanh Hằng', 'dothanhhang@vju.vnu.edu.vn', 'rZXBqLsogVtl0FNbz2j2inNmISUcnHB9SPQfLeqF+Mw=', 'CbvmfFb0c1mdOS6opKw2WwpMlZXEtgA8TK2Mun5QkkoHb6BodsRpXtboz43FX25SoHUsaB4l3x36wVuxFTPUr2XFNgn30Jhnu34KEWZTCXw='),
(6, 'Vũ Đức Thắng', 'vuducthang@vju.vnu.edu.vn', '55P/6zttVMoZARj0x1cQymGTd5D1u9tRfRYcoFKMDM0=', 'RDc9AHIbOkmKmDsBh84x25RVXxXSB4V1k6NUAkK3EJ6TvkRKGc8J1UbszQfPng6/BKy10tg0JD4t+sdDf0HNEilhKyPFoTG889vap7MCbLol4LlG5riD8c1JPk0GBrgD'),
(7, 'Hoàng Bích Ngọc', 'hoangbichngoc@vju.vnu.edu.vn', 'uWWIDXlbwnChP0hLRpSSPy81CTauGPW8J17vNFIdq+c=', '6B2ejAILFZFZ8zQZezVpSyVSNVoePB6kWmiv8RtK/2VVzza83R7nnJtHIkE9CwvMO+oiWwOu++tH6JKSaKj4qOFZp+F4NEZGCwAfa+y1lWw='),
(8, 'Bùi Quang Huy', 'buiquanghuy@vju.vnu.edu.vn', 'IHOKM4NGRqLWBWxqhvqBdz3FNTHU6aUllNMnFcjOIdo=', 'wkiLIBxqK6VV5/oDon1K2oRSVzBZSwLuJQar9J9+MQEcUT8X5eiNpCARIK0eu5mOi1YkTnpb2o6q9mMdwJZC5P3FjibALHr2Iu9IVYXlnhP4Tv6LWANnRHZfyJ1zpcZx'),
(9, 'Đặng Thu Thảo', 'dangthuthao@vju.vnu.edu.vn', 'MCAUI2/JjEJeoCP7Z8dNsjNiv84OhNUnvaPsmLRsmT8=', 'AmjXmx9FuhDuPBhHPU8ZsY8S0aVk0sKvemjUkLweJ9rpbYl2vKljx3t4OIdMmOB697tiuRu+z9msZWnXU41UTvuGxj+LM5YXlSzLYDYyg74yAv09H0Nt3y0hyt21mWT7j18M2Euna0TnvtH7/wkZAA=='),
(10, 'Ngô Gia Bảo', 'ngogiabao@vju.vnu.edu.vn', '6unNTTDWg4pm/DFhX0Yl2dUFjgsLNXEo0Vyei9LRNTw=', 'uElBsXAUvSqzaRL3vtBrlkMJDr/BcST7l+IkUAI0f/0QhlzU6aVhiuE85wx4Y3WiTquuae7mZxink3th4YOGva2QDXUr9z+2y01e2YBWM62BSrQAZyKbmQrT2SRR05Qt'),
(11, 'Trịnh Khánh Linh', 'trinhkhanhlinh@vju.vnu.edu.vn', 'Yvtrt1oPQYKy4GVSIAhBgU3vgKcWm0wxs1rZNS7+308=', '28zr21/rB8zJ/zGovSwX2cSbA811OYVsuqu9TrMGBmhAPCnLisV2PKcKIxCh2xju+YpJahzr5SWTg59sHPKHUbfWqXthjwUdvIEbGJ43Ar/9bakth4WvQdRCYojqEIZl'),
(12, 'Phan Anh Đức', 'phananhduc@vju.vnu.edu.vn', 'jsHhJWVndry5S+Mg/DFZckaQP8fQLN+nv9+QZaFunu0=', 'AbKSyNoFQBoG3PpHYmvpwCr4VYOI1KyTt2DBtTjT28DhKTQhHPB7GE0QnEptMDAsOd+r0yGMQ5VS7mJWaqNyzI1eYbfjAXBxgcvL2Bqd4nNLXcHe+PiGd8o8XfodxhRlI028enFjk2YkkFR9CxTOrQ==');

-- 4. Thêm danh sách Tài liệu Mẫu Thuần Việt (documents)
INSERT INTO `documents` (`id`, `app_detail_fk`, `name`, `path`, `owner`, `version`, `deleted`, `active`, `created_time`, `modified_time`) VALUES
('doc-0001-java-spring', '1', 'Giao-trinh-Java-Spring-Boot-Chuyen-Sau.pdf', '/documents/courses/1/giao-trinh.pdf', 'admin', 1.0, 0, 1, NOW(), NOW()),
('doc-0002-dsa-outline', '2', 'De-cuong-mon-hoc-Cau-truc-du-lieu.pdf', '/documents/courses/2/de-cuong.pdf', 'lecturer', 1.0, 0, 1, NOW(), NOW()),
('doc-0003-mysql-lab', '3', 'Huong-dan-thuc-hanh-MySQL.docx', '/documents/courses/3/thuc-hanh.docx', 'admin', 1.0, 0, 1, NOW(), NOW()),
('doc-0004-microservices', '5', 'Tai-lieu-Kien-truc-Microservices.pdf', '/documents/courses/5/microservices.pdf', 'admin', 1.0, 0, 1, NOW(), NOW()),
('doc-0005-student-resume', '1', 'So-yeu-ly-lich-Nguyen-Van-An.pdf', '/documents/students/1/ly-lich.pdf', 'student', 1.0, 0, 1, NOW(), NOW()),
('doc-0006-student-grades', '2', 'Bang-diem-hoc-tap-Tran-Thi-Mai-Hoa.xlsx', '/documents/students/2/bang-diem.xlsx', 'student', 1.0, 0, 1, NOW(), NOW());
