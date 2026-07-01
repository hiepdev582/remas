package com.hiepnn.remas.feature.customer.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hiepnn.remas.entity.CustomerDocument;

public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Integer> {
    List<CustomerDocument> findByCustomerId(Integer customerId);

    void deleteByCustomerId(Integer customerId);
}
