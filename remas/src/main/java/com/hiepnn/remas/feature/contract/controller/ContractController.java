package com.hiepnn.remas.feature.contract.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.feature.contract.model.ContractRequest;
import com.hiepnn.remas.feature.contract.model.ContractResponse;
import com.hiepnn.remas.feature.contract.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import com.hiepnn.remas.common.constant.ContractStatus;

@RestController
@RequestMapping("/api/v1/contract")
@Tag(name = "Contract Management", description = "Endpoints API quản lý hợp đồng")
public class ContractController {
    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Danh sách hợp đồng theo bộ lọc", description = "Lấy danh sách hợp đồng theo bộ lọc")
    public ResponseEntity<PagingResponse<ContractResponse>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) List<ContractStatus> status) {
        return ResponseEntity.ok(contractService.getContractsWithFilter(page, pageSize, search, sortField, sortOrder, status));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Chi tiết hợp đồng", description = "Lấy thông tin chi tiết hợp đồng theo ID")
    public ResponseEntity<ContractResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Tạo hợp đồng mới", description = "Thêm mới hợp đồng")
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request) {
        ContractResponse response = contractService.createContract(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Cập nhật hợp đồng", description = "Cập nhật thông tin hợp đồng")
    public ResponseEntity<ContractResponse> update(@PathVariable Integer id,
            @Valid @RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.updateContract(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Hủy/Xóa hợp đồng", description = "Đánh dấu trạng thái hợp đồng thành CANCELLED")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
}
