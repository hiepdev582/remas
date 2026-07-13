package com.hiepnn.remas.feature.report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiepnn.remas.feature.report.model.DashboardReportResponse;
import com.hiepnn.remas.feature.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "Report Management", description = "Endpoints API báo cáo thống kê")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Báo cáo thống kê Dashboard", description = "Lấy các chỉ số thống kê tổng hợp hiển thị trên Dashboard")
    public ResponseEntity<DashboardReportResponse> getDashboardReport() {
        return ResponseEntity.ok(reportService.getDashboardReport());
    }
}
