package com.hiepnn.remas.feature.customer.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.hiepnn.remas.common.constant.ContractStatus;
import com.hiepnn.remas.feature.contract.repository.ContractRepository;
import com.hiepnn.remas.common.annotation.Auditable;
import com.hiepnn.remas.common.constant.AuditAction;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService { 

    private final CustomerRepository customerRepository;
    private final CustomerDocumentRepository customerDocumentRepository;
    private final ContractRepository contractRepository;

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

        Integer age = customer.getDob() != null 
                ? Period.between(customer.getDob(), LocalDate.now()).getYears() 
                : null;
        Long daysSinceLastInteraction = customer.getLastInteractionDate() != null 
                ? ChronoUnit.DAYS.between(customer.getLastInteractionDate().toLocalDate(), LocalDate.now()) 
                : null;

        java.math.BigDecimal revenue = contractRepository.sumFinalAmountByCustomerIdAndStatusNot(customer.getId(), ContractStatus.CANCELLED);
        Long rentalCount = contractRepository.countByCustomerIdAndStatusNot(customer.getId(), ContractStatus.CANCELLED);

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .identityCard(customer.getIdentityCard())
                .driverLicense(customer.getDriverLicense())
                .trustScore(customer.getTrustScore())
                .gender(customer.getGender())
                .dob(customer.getDob())
                .age(age)
                .address(customer.getAddress())
                .lastInteractionDate(customer.getLastInteractionDate())
                .daysSinceLastInteraction(daysSinceLastInteraction)
                .note(customer.getNote())
                .link(customer.getLink())
                .revenue(revenue)
                .rentalCount(rentalCount)
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

        int pageIndex = Math.max(0, page - 1);
        boolean isCalculatedSort = "rentalCount".equalsIgnoreCase(sortField) || "revenue".equalsIgnoreCase(sortField);

        Sort.Direction direction = Sort.Direction.DESC;
        if (sortOrder != null) {
            direction = "descend".equalsIgnoreCase(sortOrder) || "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        }

        if (isCalculatedSort) {
            List<Customer> allCustomers = customerRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));
            List<CustomerResponse> responses = allCustomers.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            if ("rentalCount".equalsIgnoreCase(sortField)) {
                if (direction == Sort.Direction.ASC) {
                    responses.sort(Comparator.comparing(CustomerResponse::getRentalCount));
                } else {
                    responses.sort(Comparator.comparing(CustomerResponse::getRentalCount).reversed());
                }
            } else if ("revenue".equalsIgnoreCase(sortField)) {
                if (direction == Sort.Direction.ASC) {
                    responses.sort(Comparator.comparing(CustomerResponse::getRevenue, 
                            Comparator.nullsFirst(Comparator.naturalOrder())));
                } else {
                    responses.sort(Comparator.comparing(CustomerResponse::getRevenue, 
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                }
            }

            int total = responses.size();
            int fromIndex = pageIndex * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);
            
            List<CustomerResponse> pagedContent = new ArrayList<>();
            if (fromIndex < total) {
                pagedContent = responses.subList(fromIndex, toIndex);
            }

            return PagingResponse.<CustomerResponse>builder()
                    .data(pagedContent)
                    .total((long) total)
                    .build();
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
            if (sortField != null && !sortField.trim().isEmpty()) {
                sort = Sort.by(direction, sortField);
            }

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
    }

    //#region All
    @Transactional(readOnly = true)
    public PagingResponse<CustomerResponse> getAllCustomers() {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();

        Specification<Customer> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("isDeleted"), false));

            if (!isSuperAdmin && username != null) {
                p = cb.and(p, cb.equal(root.get("createdBy"), username));
            }
            return p;
        };

        List<Customer> list = customerRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));
        List<CustomerResponse> content = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagingResponse.<CustomerResponse>builder()
                .data(content)
                .total(content.size())
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
    @Auditable(action = AuditAction.CREATE_CUSTOMER, description = "'Created new customer: ' + #result.name + ' (' + #result.phone + ')'")
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByPhoneAndIsDeletedFalse(request.getPhone())) {
            throw new BadRequestException("Phone number already exists!");
        }
        String identityCard = (request.getIdentityCard() != null && !request.getIdentityCard().trim().isEmpty())
                ? request.getIdentityCard().trim()
                : null;
        String driverLicense = (request.getDriverLicense() != null && !request.getDriverLicense().trim().isEmpty())
                ? request.getDriverLicense().trim()
                : null;

        if (identityCard != null && customerRepository.existsByIdentityCardAndIsDeletedFalse(identityCard)) {
            throw new BadRequestException("Identity Card already exists!");
        }
        if (driverLicense != null && customerRepository.existsByDriverLicenseAndIsDeletedFalse(driverLicense)) {
            throw new BadRequestException("Driver License already exists!");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .identityCard(identityCard)
                .driverLicense(driverLicense)
                .gender(request.getGender())
                .dob(request.getDob())
                .address(request.getAddress())
                .lastInteractionDate(request.getLastInteractionDate())
                .note(request.getNote())
                .link(request.getLink())
                .trustScore(request.getTrustScore() != null ? request.getTrustScore() : 100)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        saveDocuments(savedCustomer, request.getDocuments());

        return mapToResponse(savedCustomer);
    }
    //#endregion

    //#region Update
    @Transactional
    @Auditable(action = AuditAction.UPDATE_CUSTOMER, description = "'Updated customer: ' + #result.name")
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

        String identityCard = (request.getIdentityCard() != null && !request.getIdentityCard().trim().isEmpty())
                ? request.getIdentityCard().trim()
                : null;
        String driverLicense = (request.getDriverLicense() != null && !request.getDriverLicense().trim().isEmpty())
                ? request.getDriverLicense().trim()
                : null;

        if (customerRepository.existsByPhoneAndIdNotAndIsDeletedFalse(request.getPhone(), id)) {
            throw new BadRequestException("Phone number already exists!");
        }
        if (identityCard != null && customerRepository.existsByIdentityCardAndIdNotAndIsDeletedFalse(identityCard, id)) {
            throw new BadRequestException("Identity Card already exists!");
        }
        if (driverLicense != null && customerRepository.existsByDriverLicenseAndIdNotAndIsDeletedFalse(driverLicense, id)) {
            throw new BadRequestException("Driver License already exists!");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setIdentityCard(identityCard);
        customer.setDriverLicense(driverLicense);
        customer.setGender(request.getGender());
        customer.setDob(request.getDob());
        customer.setAddress(request.getAddress());
        customer.setLastInteractionDate(request.getLastInteractionDate());
        customer.setNote(request.getNote());
        customer.setLink(request.getLink());
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
    @Auditable(action = AuditAction.DELETE_CUSTOMER, description = "'Deleted customer #' + #id")
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
