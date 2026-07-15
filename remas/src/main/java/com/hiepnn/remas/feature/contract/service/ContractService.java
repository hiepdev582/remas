package com.hiepnn.remas.feature.contract.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.hiepnn.remas.common.constant.ContractStatus;
import com.hiepnn.remas.entity.Contract;
import com.hiepnn.remas.entity.ContractCollateral;
import com.hiepnn.remas.entity.ContractDetail;
import com.hiepnn.remas.entity.ContractFee;
import com.hiepnn.remas.entity.Customer;
import com.hiepnn.remas.entity.Item;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.feature.contract.model.*;
import com.hiepnn.remas.feature.contract.repository.*;
import com.hiepnn.remas.feature.customer.repository.CustomerRepository;
import com.hiepnn.remas.feature.inventory.item.repository.ItemRepository;
import com.hiepnn.remas.util.SecurityUtils;

import com.hiepnn.remas.common.annotation.Auditable;
import com.hiepnn.remas.common.constant.AuditAction;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractDetailRepository contractDetailRepository;
    private final ContractCollateralRepository contractCollateralRepository;
    private final ContractFeeRepository contractFeeRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    private ContractResponse mapToResponse(Contract contract) {
        List<ContractDetail> details = contractDetailRepository.findByContractId(contract.getId());
        List<ContractCollateral> collaterals = contractCollateralRepository.findByContractId(contract.getId());
        List<ContractFee> fees = contractFeeRepository.findByContractId(contract.getId());

        List<ContractDetailResponse> detailResponses = details.stream()
                .map(d -> ContractDetailResponse.builder()
                        .id(d.getId())
                        .itemId(d.getItem().getId())
                        .itemName(d.getItem().getName())
                        .quantity(d.getQuantity())
                        .appliedPriceType(d.getAppliedPriceType())
                        .priceApplied(d.getPriceApplied())
                        .subtotal(d.getSubtotal())
                        .handoverStatus(d.getHandoverStatus())
                        .build())
                .collect(Collectors.toList());

        List<ContractCollateralResponse> collateralResponses = collaterals.stream()
                .map(c -> ContractCollateralResponse.builder()
                        .id(c.getId())
                        .collateralType(c.getCollateralType())
                        .value(c.getValue())
                        .assetDescription(c.getAssetDescription())
                        .status(c.getStatus())
                        .build())
                .collect(Collectors.toList());

        List<ContractFeeResponse> feeResponses = fees.stream()
                .map(f -> ContractFeeResponse.builder()
                        .id(f.getId())
                        .feeType(f.getFeeType())
                        .amount(f.getAmount())
                        .pickupLocation(f.getPickupLocation())
                        .returnLocation(f.getReturnLocation())
                        .note(f.getNote())
                        .build())
                .collect(Collectors.toList());

        return ContractResponse.builder()
                .id(contract.getId())
                .customerId(contract.getCustomer().getId())
                .customerName(contract.getCustomer().getName())
                .customerPhone(contract.getCustomer().getPhone())
                .startDate(contract.getStartDate())
                .expectedReturnDate(contract.getExpectedReturnDate())
                .actualReturnDate(contract.getActualReturnDate())
                .totalItemPrice(contract.getTotalItemPrice())
                .totalFeePrice(contract.getTotalFeePrice())
                .finalAmount(contract.getFinalAmount())
                .collateralStatus(contract.getCollateralStatus())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .createdBy(contract.getCreatedBy())
                .updatedBy(contract.getUpdatedBy())
                .details(detailResponses)
                .collaterals(collateralResponses)
                .fees(feeResponses)
                .build();
    }

    //#region List
    @Transactional(readOnly = true)
    public PagingResponse<ContractResponse> getContractsWithFilter(int page, int pageSize, String search,
            String sortField, String sortOrder, List<ContractStatus> statusList) {
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();

        Specification<Contract> spec = (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (!isSuperAdmin && username != null) {
                p = cb.and(p, cb.equal(root.get("createdBy"), username));
            }

            if (statusList != null && !statusList.isEmpty()) {
                p = cb.and(p, root.get("status").in(statusList));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Join<Contract, Customer> customerJoin = root.join("customer", JoinType.LEFT);
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(customerJoin.get("name")), searchPattern),
                        cb.like(cb.lower(customerJoin.get("phone")), searchPattern));
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
        Page<Contract> contractPage = contractRepository.findAll(spec, pageable);

        List<ContractResponse> content = contractPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagingResponse.<ContractResponse>builder()
                .data(content)
                .total(contractPage.getTotalElements())
                .build();
    }
    //#endregion

    //#region Detail
    @Transactional(readOnly = true)
    public ContractResponse getContractById(Integer id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found!"));

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(contract.getCreatedBy())) {
            throw new ResourceNotFoundException("Contract not found!");
        }
        return mapToResponse(contract);
    }
    //#endregion

    //#region Create
    @Transactional
    @Auditable(action = AuditAction.CREATE_CONTRACT, description = "'Created new contract for customer: ' + #result.customerName")
    public ContractResponse createContract(ContractRequest request) {
        checkItemAvailability(request.getDetails(), request.getStartDate(), request.getExpectedReturnDate(), null);

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found!"));

        Contract contract = Contract.builder()
                .customer(customer)
                .startDate(request.getStartDate())
                .expectedReturnDate(request.getExpectedReturnDate())
                .actualReturnDate(request.getActualReturnDate())
                .collateralStatus(request.getCollateralStatus())
                .status(request.getStatus())
                .build();

        Contract savedContract = contractRepository.save(contract);

        BigDecimal totalItemPrice = saveDetails(savedContract, request.getDetails());
        BigDecimal totalFeePrice = saveFees(savedContract, request.getFees());
        saveCollaterals(savedContract, request.getCollaterals());

        savedContract.setTotalItemPrice(totalItemPrice);
        savedContract.setTotalFeePrice(totalFeePrice);
        savedContract.setFinalAmount(totalItemPrice.add(totalFeePrice));

        contractRepository.save(savedContract);

        return mapToResponse(savedContract);
    }
    //#endregion

    //#region Update
    @Transactional
    @Auditable(action = AuditAction.UPDATE_CONTRACT, description = "'Updated contract #' + #id")
    public ContractResponse updateContract(Integer id, ContractRequest request) {
        checkItemAvailability(request.getDetails(), request.getStartDate(), request.getExpectedReturnDate(), id);

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found!"));

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(contract.getCreatedBy())) {
            throw new ResourceNotFoundException("Contract not found!");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found!"));

        contract.setCustomer(customer);
        contract.setStartDate(request.getStartDate());
        contract.setExpectedReturnDate(request.getExpectedReturnDate());
        contract.setActualReturnDate(request.getActualReturnDate());
        contract.setCollateralStatus(request.getCollateralStatus());
        contract.setStatus(request.getStatus());

        // Replace details
        contractDetailRepository.deleteByContractId(id);
        BigDecimal totalItemPrice = saveDetails(contract, request.getDetails());

        // Replace fees
        contractFeeRepository.deleteByContractId(id);
        BigDecimal totalFeePrice = saveFees(contract, request.getFees());

        // Replace collaterals
        contractCollateralRepository.deleteByContractId(id);
        saveCollaterals(contract, request.getCollaterals());

        contract.setTotalItemPrice(totalItemPrice);
        contract.setTotalFeePrice(totalFeePrice);
        contract.setFinalAmount(totalItemPrice.add(totalFeePrice));

        Contract savedContract = contractRepository.save(contract);

        return mapToResponse(savedContract);
    }
    //#endregion

    //#region Delete
    @Transactional
    @Auditable(action = AuditAction.CANCEL_CONTRACT, description = "'Cancelled contract #' + #id")
    public void deleteContract(Integer id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found!"));

        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
        String username = SecurityUtils.getCurrentUsername();
        if (!isSuperAdmin && username != null && !username.equals(contract.getCreatedBy())) {
            throw new ResourceNotFoundException("Contract not found!");
        }

        // Cancel the contract
        contract.setStatus(ContractStatus.CANCELLED);
        contractRepository.save(contract);
    }
    //#endregion

    private BigDecimal saveDetails(Contract contract, List<ContractDetailRequest> detailRequests) {
        if (detailRequests == null || detailRequests.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        List<ContractDetail> details = new ArrayList<>();

        for (ContractDetailRequest req : detailRequests) {
            Item item = itemRepository.findById(req.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + req.getItemId()));

            ContractDetail detail = ContractDetail.builder()
                    .contract(contract)
                    .item(item)
                    .quantity(req.getQuantity())
                    .appliedPriceType(req.getAppliedPriceType())
                    .priceApplied(req.getPriceApplied())
                    .subtotal(req.getSubtotal())
                    .handoverStatus(req.getHandoverStatus())
                    .build();

            details.add(detail);
            total = total.add(req.getSubtotal());
        }

        contractDetailRepository.saveAll(details);
        return total;
    }

    private BigDecimal saveFees(Contract contract, List<ContractFeeRequest> feeRequests) {
        if (feeRequests == null || feeRequests.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        List<ContractFee> fees = new ArrayList<>();

        for (ContractFeeRequest req : feeRequests) {
            ContractFee fee = ContractFee.builder()
                    .contract(contract)
                    .feeType(req.getFeeType())
                    .amount(req.getAmount())
                    .pickupLocation(req.getPickupLocation())
                    .returnLocation(req.getReturnLocation())
                    .note(req.getNote())
                    .build();

            fees.add(fee);
            total = total.add(req.getAmount());
        }

        contractFeeRepository.saveAll(fees);
        return total;
    }

    private void saveCollaterals(Contract contract, List<ContractCollateralRequest> collateralRequests) {
        if (collateralRequests == null || collateralRequests.isEmpty()) {
            return;
        }

        List<ContractCollateral> collaterals = collateralRequests.stream()
                .map(req -> ContractCollateral.builder()
                        .contract(contract)
                        .collateralType(req.getCollateralType())
                        .value(req.getValue())
                        .assetDescription(req.getAssetDescription())
                        .status(req.getStatus())
                        .build())
                .collect(Collectors.toList());

        contractCollateralRepository.saveAll(collaterals);
    }

    private void checkItemAvailability(List<ContractDetailRequest> details, LocalDateTime startDate, LocalDateTime expectedReturnDate, Integer excludeContractId) {
        if (details == null || details.isEmpty()) {
            return;
        }

        List<Integer> itemIds = details.stream()
                .map(ContractDetailRequest::getItemId)
                .collect(Collectors.toList());

        List<ContractStatus> activeStatuses = List.of(
                ContractStatus.RESERVED,
                ContractStatus.ACTIVE,
                ContractStatus.OVERDUE
        );

        List<ContractDetail> overlappingDetails = contractDetailRepository.findOverlappingDetails(
                itemIds,
                activeStatuses,
                startDate,
                expectedReturnDate,
                excludeContractId
        );

        if (!overlappingDetails.isEmpty()) {
            String conflictedItems = overlappingDetails.stream()
                    .map(cd -> cd.getItem().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Item(s) already rented/reserved during this time range: " + conflictedItems);
        }
    }
}
