export type TopCustomerReportItem = {
  id: number;
  name: string;
  phone: string;
  value: number;
};

export type MonthlyRevenueItem = {
  month: string;
  revenue: number;
};

export type TopItemReportItem = {
  id: number;
  name: string;
  rentCount: number;
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
  monthlyRevenue?: MonthlyRevenueItem[];
  topRentedItems?: TopItemReportItem[];
  cancellationRate?: number;
};

export type StatsReportItem = {
  id: number | string;
  name: string;
  subText?: string;
  value: number;
};
