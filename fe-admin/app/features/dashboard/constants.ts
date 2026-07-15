export const indexReports = [
  // Tổng số khách đặt
  {
    key: "total-customers",
    title: "Total customers",
    icon: "carbon:customer",
  },
  // Tổng số đơn
  {
    key: "total-orders",
    title: "Total orders",
    icon: "boxicons:trip",
  },
  // Tổng doanh thu
  { key: "total-revenue", title: "Total revenue", icon: "mdi:money-100" },
  // Trung bình doanh thu mỗi đơn
  {
    key: "average-revenue-per-order",
    title: "Avg revenue",
    icon: "grommet-icons:money",
  },
  // Tổng số ngày cho thuê - Số tháng / năm
  {
    key: "total-time-of-orders",
    title: "Total rental time",
    icon: "mingcute:time-line",
  },
  // Trung bình số ngày thuê mỗi khách
  {
    key: "average-time-per-customer",
    title: "Avg rental time",
    icon: "tdesign:user-time",
  },
  // Trung bình số lần thuê mỗi khách
  {
    key: "rental-times-per-customer",
    title: "Avg number rentals",
    icon: "hugeicons:car-time",
  },
  // Tỷ lệ hủy đơn
  {
    key: "cancellation-rate",
    title: "Cancellation rate",
    icon: "material-symbols:cancel-presentation-outline",
  },
] as const;
