export type TopCustomerReportItem = {
  id: number;
  name: string;
  phone: string;
  value: number;
};

export type DashboardReport = {
  totalCustomers: number;
  totalOrders: number;
  totalRevenue: number;
  averageRevenue: number;
  totalRentalTime: number;
  averageRentalTime: number;
  rentalTimesPerCustomer: number;
  topRevenueCustomers: TopCustomerReportItem[];
  topCountCustomers: TopCustomerReportItem[];
};
