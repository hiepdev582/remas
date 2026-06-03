# 🏎️ Giải pháp tối ưu: Combo In-Memory kết hợp HttpOnly Cookie

Để vừa tối ưu bảo mật, vừa dễ scale cho ứng dụng dạng Single Page Application (React, Vue, Angular), các hệ thống lớn hiện nay thường chia JWT thành 2 loại với 2 nơi lưu trữ khác nhau:

---

### 🔑 1. Access Token (Hạn ngắn: 5 - 15 phút) $\rightarrow$ Lưu ở In-Memory (Biến trong Code Frontend)

* **Cơ chế:** Khi user đăng nhập thành công, server trả về Access Token trong body JSON.
* **Lưu trữ:** Frontend lưu token này vào một biến cục bộ (ví dụ: `const accessToken = ...` hoặc lưu trong `Vuex`/`Pinia`/`Redux` state).
* **Đặc điểm:** Do lưu trong bộ nhớ RAM của tab trình duyệt, JavaScript độc hại bên ngoài rất khó tiếp cận (Chống XSS tốt). Khi user F5 (refresh) lại trang, biến này sẽ bị xóa mất.

---

### 🔄 2. Refresh Token (Hạn dài: 7 ngày - 30 ngày) $\rightarrow$ Lưu ở HttpOnly Cookie

* **Cơ chế:** Server trả về Refresh Token thông qua một HttpOnly Cookie.
* **Hoạt động:** Khi user F5 lại trang (hoặc khi Access Token hết hạn), Frontend sẽ tự động gửi một request ngầm (silent refresh) lên endpoint `/refresh-token` của Backend. Do trình duyệt tự đính kèm HttpOnly Cookie, Backend sẽ xác thực và cấp lại một cặp Access Token mới (lưu lại vào RAM) và Refresh Token mới.
