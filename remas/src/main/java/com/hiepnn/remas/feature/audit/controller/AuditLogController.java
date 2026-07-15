package com.hiepnn.remas.feature.audit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.feature.audit.model.AuditLogResponse;
import com.hiepnn.remas.feature.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log API", description = "Quản lý và xem lịch sử thao tác hệ thống")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Lấy danh sách lịch sử thao tác hệ thống (Audit logs) có phân trang và lọc")
    public ResponseEntity<PagingResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        
        return ResponseEntity.ok(auditLogService.getAuditLogsWithFilter(page, pageSize, search, sortField, sortOrder));
    }
}
