package com.hiepnn.remas.feature.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hiepnn.remas.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    
    @Query("SELECT c FROM Customer c WHERE " +
           "c.isDeleted = false AND " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.identityCard) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.driverLicense) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Customer> searchCustomers(@Param("search") String search, Pageable pageable);

    boolean existsByPhoneAndIsDeletedFalse(String phone);
    
    boolean existsByPhoneAndIdNotAndIsDeletedFalse(String phone, Integer id);

    boolean existsByIdentityCardAndIsDeletedFalse(String identityCard);
    
    boolean existsByIdentityCardAndIdNotAndIsDeletedFalse(String identityCard, Integer id);

    boolean existsByDriverLicenseAndIsDeletedFalse(String driverLicense);
    
    boolean existsByDriverLicenseAndIdNotAndIsDeletedFalse(String driverLicense, Integer id);
}
