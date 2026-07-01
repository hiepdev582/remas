package com.hiepnn.remas.feature.customer.controller;

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
import com.hiepnn.remas.feature.customer.model.CustomerRequest;
import com.hiepnn.remas.feature.customer.model.CustomerResponse;
import com.hiepnn.remas.feature.customer.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer")
@Tag(name = "Customer Management", description = "Endpoints API quản lý khách hàng")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    @Operation(summary = "Danh sách khách hàng theo bộ lọc", description = "Lấy danh sách khách hàng theo bộ lọc")
    public ResponseEntity<PagingResponse<CustomerResponse>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        return ResponseEntity.ok(customerService.getCustomersWithFilter(page, pageSize, search, sortField, sortOrder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết khách hàng", description = "Lấy thông tin chi tiết khách hàng theo ID")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Tạo khách hàng mới", description = "Thêm mới khách hàng và tài liệu đi kèm")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Cập nhật khách hàng", description = "Cập nhật thông tin và danh sách tài liệu khách hàng")
    public ResponseEntity<CustomerResponse> update(@PathVariable Integer id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Xóa khách hàng", description = "Xóa thông tin khách hàng và tất cả tài liệu đi kèm")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
