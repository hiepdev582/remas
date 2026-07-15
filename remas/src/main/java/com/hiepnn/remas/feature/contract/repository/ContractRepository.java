package com.hiepnn.remas.feature.contract.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hiepnn.remas.entity.Contract;
import com.hiepnn.remas.common.constant.ContractStatus;

public interface ContractRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {
    @Query("SELECT COALESCE(SUM(c.finalAmount), 0) FROM Contract c WHERE c.customer.id = :customerId AND c.status <> :status")
    BigDecimal sumFinalAmountByCustomerIdAndStatusNot(@Param("customerId") Integer customerId,
            @Param("status") ContractStatus status);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.customer.id = :customerId AND c.status <> :status")
    long countByCustomerIdAndStatusNot(@Param("customerId") Integer customerId, @Param("status") ContractStatus status);

    List<Contract> findByStatusNot(ContractStatus status);

    List<Contract> findByStatusNotAndCreatedBy(ContractStatus status, String createdBy);
}
