package com.hiepnn.remas.feature.contract.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hiepnn.remas.entity.ContractCollateral;

public interface ContractCollateralRepository extends JpaRepository<ContractCollateral, Integer> {
    List<ContractCollateral> findByContractId(Integer contractId);

    void deleteByContractId(Integer contractId);
}
