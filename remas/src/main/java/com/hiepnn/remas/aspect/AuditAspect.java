package com.hiepnn.remas.aspect;

import java.time.LocalDateTime;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.hiepnn.remas.common.annotation.Auditable;
import com.hiepnn.remas.common.constant.AuditAction;
import com.hiepnn.remas.entity.AuditLog;
import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.feature.auth.model.LoginRequest;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.audit.repository.AuditLogRepository;
import com.hiepnn.remas.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ExpressionParser spelParser = new SpelExpressionParser();

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void auditAction(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            String username = SecurityUtils.getCurrentUsername();
            if (username == null) {
                if (auditable.action() == AuditAction.LOGIN && joinPoint.getArgs().length > 0) {
                    Object firstArg = joinPoint.getArgs()[0];
                    if (firstArg instanceof LoginRequest) {
                        username = ((LoginRequest) firstArg).getUsername();
                    }
                }
            }
            if (username == null) {
                log.warn("Auditing skipped: no authenticated user found");
                return;
            }

            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.warn("Auditing skipped: user entity not found for username: {}", username);
                return;
            }

            // Evaluate SpEL description
            String description = auditable.description();
            if (!description.isEmpty()) {
                try {
                    EvaluationContext evalContext = new StandardEvaluationContext();
                    
                    // Bind method arguments
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    String[] parameterNames = signature.getParameterNames();
                    Object[] args = joinPoint.getArgs();
                    
                    if (parameterNames != null) {
                        for (int i = 0; i < parameterNames.length; i++) {
                            evalContext.setVariable(parameterNames[i], args[i]);
                        }
                    }
                    
                    // Bind return result as well
                    evalContext.setVariable("result", result);
                    
                    description = spelParser.parseExpression(description).getValue(evalContext, String.class);
                } catch (Exception e) {
                    log.error("Failed to parse SpEL description for audit log: {}", auditable.description(), e);
                    description = auditable.description(); // Fallback to raw expression
                }
            }

            // Get Request IP Address
            String ipAddress = "unknown";
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                }
            }

            AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(auditable.action())
                .description(description)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .build();

            auditLogRepository.save(auditLog);
            log.info("Successfully saved audit log for action: {} by user: {}", auditable.action(), username);

        } catch (Exception e) {
            log.error("Error occurred while writing audit log", e);
        }
    }
}
