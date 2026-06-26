package com.hiepnn.remas.feature.user.controller;

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

import java.util.List;

import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.feature.user.model.UserRequest;
import com.hiepnn.remas.feature.user.model.UserResponse;
import com.hiepnn.remas.feature.user.model.UserStatusRequest;
import com.hiepnn.remas.feature.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Management", description = "Endpoints API quản lý người dùng")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @Operation(summary = "Danh sách tất cả người dùng hoạt động", description = "Lấy tất cả người dùng hoạt động")
  public ResponseEntity<PagingResponse<UserResponse>> getAll() {
    return ResponseEntity.ok(userService.getAllActiveUsers());
  }

  @GetMapping("/list")
  @Operation(summary = "Danh sách người dùng theo bộ lọc", description = "Lấy người dùng theo bộ lọc")
  public ResponseEntity<PagingResponse<UserResponse>> getList(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false) String sortOrder,
      @RequestParam(required = false) List<Boolean> isActive,
      @RequestParam(required = false) List<String> roles) {
    return ResponseEntity.ok(userService.getUsersWithFilter(page, pageSize, search, sortField, sortOrder, isActive, roles));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Chi tiết người dùng", description = "Lấy người dùng theo id")
  public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Tạo người dùng mới", description = "Tạo người dùng mới")
  public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
    UserResponse response = userService.createUser(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin người dùng")
  public ResponseEntity<UserResponse> update(@PathVariable Integer id, @Valid @RequestBody UserRequest request) {
    return ResponseEntity.ok(userService.updateUser(id, request));
  }

  @PutMapping("/update-status/{id}")
  @PreAuthorize("hasAuthority('SUPER_ADMIN')")
  @Operation(summary = "Cập nhật trạng thái người dùng", description = "Cập nhật trạng thái người dùng")
  public ResponseEntity<UserResponse> updateStatus(@PathVariable Integer id, @Valid @RequestBody UserStatusRequest request) {
    return ResponseEntity.ok(userService.updateUserStatus(id, request));
  }
}
