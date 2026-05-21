# Phân tích: Việc đo thời gian thủ công vi phạm nguyên tắc thiết kế nào?

## 1) Bối cảnh
Trong `com.bank.digital.transaction.service.TransactionService`, mỗi hàm giao dịch bị “cài” thủ công đoạn:
- `long startTime = System.currentTimeMillis();`
- `long endTime = System.currentTimeMillis();`
- in ra thời gian chạy trong `ms`.

Điều này làm xuất hiện **code lặp lại** ở nhiều phương thức, gây rối và khó bảo trì.

---

## 2) Vi phạm nguyên tắc/thiết kế nào?

### (a) Vi phạm Separation of Concerns (Tách biệt mối quan tâm)
- **Mối quan tâm nghiệp vụ**: `processPayment(...)` phải tập trung vào logic xử lý thanh toán.
- **Mối quan tâm kỹ thuật/không thuộc nghiệp vụ**: đo thời gian thực thi & log performance.

Việc gắn trực tiếp đoạn đo thời gian vào trong mỗi phương thức khiến **mối quan tâm hiệu năng (logging/monitoring)** bị trộn lẫn với **mối quan tâm nghiệp vụ (payment processing)**.

👉 Kết quả: cùng một trách nhiệm “measure+log” lẫn vào business logic, làm giảm tính trong sạch của code.

---

### (b) Code Tangling / Duplication (rối code & lặp mã)
Đo thời gian thủ công lặp lại ở nhiều hàm:
- mỗi method lại cần start/end time
- mỗi method lại cần print log giống nhau

👉 Đây là dấu hiệu của **code tangling** (trách nhiệm cross-cutting bị nhúng vào nhiều nơi) và **duplicate code**, làm:
- khó sửa đồng bộ format log
- khó thay đổi cách đo thời gian (vd: dùng logger framework, Micrometer, Stopwatch…)
- khó đảm bảo mọi method đều được đo đúng

---

### (c) Vi phạm nguyên tắc Open/Closed Principle (mở để mở rộng, đóng để sửa)
Giả sử sau này muốn:
- đổi sang dùng `Stopwatch`
- gửi metrics vào monitoring system (Prometheus/Datadog)
- đổi format log hoặc log level
- thêm ngữ cảnh (accountNumber, correlationId)

Khi đo thời gian đang “cài rải” trong từng method, thì **phải sửa nhiều file/nhiều phương thức**.
👉 Trái với Open/Closed Principle: lẽ ra có thể mở rộng bằng cơ chế chung (aspect/interceptor) mà không cần sửa từng method nghiệp vụ.

---

## 3) Kết luận
Việc đo thời gian và log thủ công bằng `System.currentTimeMillis()` trong business method vi phạm chính:
1. **Separation of Concerns** (trộn lẫn concern đo thời gian/log performance với nghiệp vụ thanh toán)
2. **Code tangling / duplication** (lặp mã, rối rắm, khó bảo trì)
3. **Open/Closed Principle** (muốn thay đổi cách đo/log phải sửa nhiều nơi)

Do đó, giải pháp đúng hướng là tách concern cross-cutting (performance logging) sang một cơ chế chung như **AOP (Aspect)** hoặc **Interceptor/Filter** thay vì chèn lặp trong từng method.