package com.hiepnn.remas.feature.audit.model;

import java.time.LocalDateTime;
import com.hiepnn.remas.common.constant.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Integer id;
    private String username;
    private String userFullName;
    private AuditAction action;
    private String description;
    private String ipAddress;
    private LocalDateTime createdAt;
}
