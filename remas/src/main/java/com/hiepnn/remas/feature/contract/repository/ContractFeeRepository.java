package com.hiepnn.remas.feature.contract.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hiepnn.remas.entity.ContractFee;

public interface ContractFeeRepository extends JpaRepository<ContractFee, Integer> {
    List<ContractFee> findByContractId(Integer contractId);

    void deleteByContractId(Integer contractId);
}
