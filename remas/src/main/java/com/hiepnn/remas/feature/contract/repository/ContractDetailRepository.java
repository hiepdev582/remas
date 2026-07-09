package com.hiepnn.remas.feature.contract.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hiepnn.remas.entity.ContractDetail;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hiepnn.remas.common.constant.ContractStatus;

public interface ContractDetailRepository extends JpaRepository<ContractDetail, Integer> {
    List<ContractDetail> findByContractId(Integer contractId);

    void deleteByContractId(Integer contractId);

    @Query("SELECT cd FROM ContractDetail cd WHERE cd.item.id IN :itemIds " +
           "AND cd.contract.status IN :statuses " +
           "AND (:excludeContractId IS NULL OR cd.contract.id <> :excludeContractId) " +
           "AND cd.contract.startDate < :endDate AND cd.contract.expectedReturnDate > :startDate")
    List<ContractDetail> findOverlappingDetails(
            @Param("itemIds") List<Integer> itemIds,
            @Param("statuses") List<ContractStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludeContractId") Integer excludeContractId);
}
