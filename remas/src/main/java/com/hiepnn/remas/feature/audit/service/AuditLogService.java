package com.hiepnn.remas.feature.audit.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.AuditLog;
import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.feature.audit.model.AuditLogResponse;
import com.hiepnn.remas.feature.audit.repository.AuditLogRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .username(log.getUser() != null ? log.getUser().getUsername() : "unknown")
                .userFullName(log.getUser() != null ? log.getUser().getFullName() : "unknown")
                .action(log.getAction())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public PagingResponse<AuditLogResponse> getAuditLogsWithFilter(int page, int pageSize, String search,
            String sortField, String sortOrder) {
        
        Specification<AuditLog> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Join<AuditLog, User> userJoin = root.join("user", JoinType.LEFT);
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("description")), searchPattern),
                        cb.like(cb.lower(root.get("ipAddress")), searchPattern),
                        cb.like(cb.lower(userJoin.get("username")), searchPattern),
                        cb.like(cb.lower(userJoin.get("fullName")), searchPattern));
                p = cb.and(p, searchPredicate);
            }
            return p;
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortField != null && !sortField.trim().isEmpty()) {
            Sort.Direction direction = "descend".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, sortField);
        }

        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<AuditLog> logPage = auditLogRepository.findAll(spec, pageable);

        List<AuditLogResponse> content = logPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagingResponse.<AuditLogResponse>builder()
                .data(content)
                .total(logPage.getTotalElements())
                .build();
    }
}
