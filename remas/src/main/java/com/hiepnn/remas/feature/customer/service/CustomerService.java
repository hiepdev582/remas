package com.hiepnn.remas.feature.customer.service;

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
import com.hiepnn.remas.entity.Customer;
import com.hiepnn.remas.entity.CustomerDocument;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.feature.customer.model.CustomerDocumentRequest;
import com.hiepnn.remas.feature.customer.model.CustomerDocumentResponse;
import com.hiepnn.remas.feature.customer.model.CustomerRequest;
import com.hiepnn.remas.feature.customer.model.CustomerResponse;
import com.hiepnn.remas.feature.customer.repository.CustomerDocumentRepository;
import com.hiepnn.remas.feature.customer.repository.CustomerRepository;
import com.hiepnn.remas.util.SecurityUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService { 

    private final CustomerRepository customerRepository;
    private final CustomerDocumentRepository customerDocumentRepository;

    private CustomerResponse mapToResponse(Customer customer) {
        List<CustomerDocument> docs = customerDocumentRepository.findByCustomerId(customer.getId());
        List<CustomerDocumentResponse> docResponses = docs.stream()
                .map(d -> CustomerDocumentResponse.builder()
                        .id(d.getId())
                        .customerId(customer.getId())
                        .documentType(d.getDocumentType())
                        .documentNumber(d.getDocumentNumber())
                        .imageUrl(d.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .identityCard(customer.getIdentityCard())
                .driverLicense(customer.getDriverLicense())
                .trustScore(customer.getTrustScore())
                .documents(docResponses)
                .build();
    }

    //#region Get List
    @Transactional(readOnly = true)
    public PagingResponse<CustomerResponse> getCustomersWithFilter(int page, int pageSize, String search, String sortField, String sortOrder) {
        boolean isSuperAdmin = com.hiepnn.remas.util.SecurityUtils.isSuperAdmin();
        String username = com.hiepnn.remas.util.SecurityUtils.getCurrentUsername();

        Specification<Customer> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("isDeleted"), false));

            if (!isSuperAdmin && username != null) {
                p = cb.and(p, cb.equal(root.get("createdBy"), username));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("phone")), searchPattern),
                    cb.like(cb.lower(root.get("identityCard")), searchPattern),
                    cb.like(cb.lower(root.get("driverLicense")), searchPattern)
                );
                p = cb.and(p, searchPredicate);
            }
            return p;
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        if (sortField != null && !sortField.trim().isEmpty()) {
            Sort.Direction direction = "descend".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            sort = Sort.by(direction, sortField);
        }

        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<Customer> customerPage = customerRepository.findAll(spec, pageable);

        List<CustomerResponse> content = customerPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagingResponse.<CustomerResponse>builder()
                .data(content)
                .total(customerPage.getTotalElements())
                .build();
    }
    //#endregion

    //#region Detail
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found!"));
        if (Boolean.TRUE.equals(customer.getIsDeleted())) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(customer.getCreatedBy())) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        return mapToResponse(customer);
    }
    //#endregion

    //#region Create
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByPhoneAndIsDeletedFalse(request.getPhone())) {
            throw new BadRequestException("Phone number already exists!");
        }
        if (request.getIdentityCard() != null && !request.getIdentityCard().trim().isEmpty() &&
            customerRepository.existsByIdentityCardAndIsDeletedFalse(request.getIdentityCard())) {
            throw new BadRequestException("Identity Card already exists!");
        }
        if (request.getDriverLicense() != null && !request.getDriverLicense().trim().isEmpty() &&
            customerRepository.existsByDriverLicenseAndIsDeletedFalse(request.getDriverLicense())) {
            throw new BadRequestException("Driver License already exists!");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .identityCard(request.getIdentityCard())
                .driverLicense(request.getDriverLicense())
                .trustScore(request.getTrustScore() != null ? request.getTrustScore() : 100)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        saveDocuments(savedCustomer, request.getDocuments());

        return mapToResponse(savedCustomer);
    }
    //#endregion

    //#region Update
    @Transactional
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found!"));
        if (Boolean.TRUE.equals(customer.getIsDeleted())) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(customer.getCreatedBy())) {
            throw new ResourceNotFoundException("Customer not found!");
        }

        if (customerRepository.existsByPhoneAndIdNotAndIsDeletedFalse(request.getPhone(), id)) {
            throw new BadRequestException("Phone number already exists!");
        }
        if (request.getIdentityCard() != null && !request.getIdentityCard().trim().isEmpty() &&
            customerRepository.existsByIdentityCardAndIdNotAndIsDeletedFalse(request.getIdentityCard(), id)) {
            throw new BadRequestException("Identity Card already exists!");
        }
        if (request.getDriverLicense() != null && !request.getDriverLicense().trim().isEmpty() &&
            customerRepository.existsByDriverLicenseAndIdNotAndIsDeletedFalse(request.getDriverLicense(), id)) {
            throw new BadRequestException("Driver License already exists!");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setIdentityCard(request.getIdentityCard());
        customer.setDriverLicense(request.getDriverLicense());
        if (request.getTrustScore() != null) {
            customer.setTrustScore(request.getTrustScore());
        }

        Customer savedCustomer = customerRepository.save(customer);

        // Delete existing docs and save the new ones
        customerDocumentRepository.deleteByCustomerId(id);
        saveDocuments(savedCustomer, request.getDocuments());

        return mapToResponse(savedCustomer);
    }
    //#endregion

    //#region Delete
    @Transactional
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found!"));
        if (Boolean.TRUE.equals(customer.getIsDeleted())) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(customer.getCreatedBy())) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        customer.setIsDeleted(true);
        customerRepository.save(customer);
    }
    //#endregion

    //#region Helper
    private void saveDocuments(Customer customer, List<CustomerDocumentRequest> docRequests) {
        if (docRequests == null || docRequests.isEmpty()) {
            return;
        }

        List<CustomerDocument> docs = docRequests.stream()
                .map(req -> CustomerDocument.builder()
                        .customer(customer)
                        .documentType(req.getDocumentType())
                        .documentNumber(req.getDocumentNumber())
                        .imageUrl(req.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        customerDocumentRepository.saveAll(docs);
    }
    //#endregion
}
