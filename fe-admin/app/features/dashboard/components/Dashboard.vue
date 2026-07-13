<script setup lang="ts">
import type { DashboardReport } from "../types";

const dashboardService = useDashboardService();
const report = ref<DashboardReport | null>(null);

const getReportValue = (key: string) => {
  if (!report.value) return "0";
  switch (key) {
    case "total-customers":
      return report.value.totalCustomers?.toString() || "0";
    case "total-orders":
      return report.value.totalOrders?.toString() || "0";
    case "total-revenue":
      return report.value.totalRevenue != null
        ? report.value.totalRevenue.toLocaleString() + "đ"
        : "0đ";
    case "average-revenue-per-order":
      return report.value.averageRevenue != null
        ? report.value.averageRevenue.toLocaleString() + "đ"
        : "0đ";
    case "total-time-of-orders":
      return report.value.totalRentalTime != null
        ? report.value.totalRentalTime.toString() + " days"
        : "0 days";
    case "average-time-per-customer":
      return report.value.averageRentalTime != null
        ? report.value.averageRentalTime.toString() + " days"
        : "0 days";
    case "rental-times-per-customer":
      return report.value.rentalTimesPerCustomer != null
        ? report.value.rentalTimesPerCustomer.toString() + " times"
        : "0 times";
    default:
      return "0";
  }
};

const fetchReport = async () => {
  try {
    report.value = await dashboardService.getDashboardReport();
  } catch (error) {
    console.error("Failed to fetch dashboard report", error);
  }
};

onMounted(() => {
  fetchReport();
});
</script>

<template>
  <section>
    <div
      class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 mb-8"
    >
      <div
        v-for="indexReport of indexReports"
        :key="indexReport.key"
        class="bg-white border border-slate-200/80 rounded p-4 shadow-sm hover:shadow-md hover:border-primary/40 transition-all duration-300 flex flex-col justify-between min-h-[100px] group cursor-pointer relative overflow-hidden"
      >
        <div class="flex justify-between items-start gap-x-1">
          <span
            class="text-sm font-medium text-slate-500 tracking-tight leading-relaxed select-none"
            >{{ indexReport.title }}</span
          >
          <div
            class="w-8 h-8 rounded-lg flex items-center justify-center bg-slate-50 text-slate-400 group-hover:bg-primary/10 group-hover:text-primary transition-all duration-300 shadow-sm border border-slate-100"
          >
            <Icon :name="indexReport.icon" size="18" />
          </div>
        </div>

        <div class="mt-2 flex items-baseline gap-2 z-10">
          <span class="text-2xl font-bold text-slate-800 tracking-tight">
            {{ getReportValue(indexReport.key) }}
          </span>
        </div>
      </div>
    </div>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <DashboardTopCustomersList
        is-currency
        title="Top 5 Revenue Customers"
        description="Customers who generated the highest final amounts"
        icon="ph:crown-bold"
        icon-class="text-amber-500"
        value-color-class="text-emerald-600"
        :items="report?.topRevenueCustomers"
      />
      <DashboardTopCustomersList
        title="Top 5 Repeat Customers"
        description="Customers with the most bookings"
        icon="ph:trend-up-bold"
        icon-class="text-indigo-500"
        value-suffix="bookings"
        value-color-class="text-indigo-600"
        :items="report?.topCountCustomers"
      />
    </div>
  </section>
</template>
