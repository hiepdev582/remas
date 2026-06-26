package com.hiepnn.remas.feature.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiepnn.remas.common.constant.RoleName;
import com.hiepnn.remas.common.dto.PagingResponse;
import com.hiepnn.remas.entity.Role;
import com.hiepnn.remas.entity.User;
import com.hiepnn.remas.entity.UserRole;
import com.hiepnn.remas.exception.BadRequestException;
import com.hiepnn.remas.exception.ResourceNotFoundException;
import com.hiepnn.remas.feature.auth.repository.RoleRepository;
import com.hiepnn.remas.feature.auth.repository.UserRepository;
import com.hiepnn.remas.feature.auth.repository.UserRoleRepository;
import com.hiepnn.remas.feature.user.model.UserRequest;
import com.hiepnn.remas.feature.user.model.UserResponse;
import com.hiepnn.remas.feature.user.model.UserStatusRequest;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  private UserResponse mapToResponse(User user) {
    List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
    List<String> roles = userRoles.stream()
        .map(ur -> ur.getRole().getName().getValue())
        .collect(Collectors.toList());

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .isActive(user.getIsActive())
        .roles(roles)
        .build();
  }

  // #region Get all
  @Transactional(readOnly = true)
  public PagingResponse<UserResponse> getAllActiveUsers() {
    List<User> list = userRepository.findAll().stream()
        .filter(User::getIsActive)
        .collect(Collectors.toList());

    List<UserResponse> responseList = list.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return PagingResponse.<UserResponse>builder()
        .data(responseList)
        .total(responseList.size())
        .page(1)
        .pageSize(responseList.size())
        .build();
  }
  // #endregion

  // #region Paging & filter
  @Transactional(readOnly = true)
  public PagingResponse<UserResponse> getUsersWithFilter(int page, int pageSize, String search, String sortField,
      String sortOrder, List<Boolean> isActive, List<String> roles) {
    Specification<User> spec = (root, query, cb) -> {
      Predicate p = cb.conjunction();

      if (isActive != null && !isActive.isEmpty()) {
        p = cb.and(p, root.get("isActive").in(isActive));
      }

      if (roles != null && !roles.isEmpty()) {
        List<RoleName> roleEnums = roles.stream()
            .map(RoleName::valueOf)
            .collect(Collectors.toList());

        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<UserRole> urRoot = subquery.from(UserRole.class);
        subquery.select(urRoot.get("user").get("id"))
            .where(urRoot.get("role").get("name").in(roleEnums));

        p = cb.and(p, root.get("id").in(subquery));
      }

      if (search != null && !search.trim().isEmpty()) {
        String searchPattern = "%" + search.trim().toLowerCase() + "%";
        Predicate searchPredicate = cb.or(
            cb.like(cb.lower(root.get("username")), searchPattern),
            cb.like(cb.lower(root.get("fullName")), searchPattern),
            cb.like(cb.lower(root.get("email")), searchPattern));
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
    Page<User> userPage = userRepository.findAll(spec, pageable);

    List<UserResponse> responseList = userPage.getContent().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return PagingResponse.<UserResponse>builder()
        .data(responseList)
        .total(userPage.getTotalElements())
        .page(page)
        .pageSize(pageSize)
        .build();
  }
  // #endregion

  // #region Get by id
  @Transactional(readOnly = true)
  public UserResponse getUserById(Integer id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    return mapToResponse(user);
  }
  // #endregion

  private void validatePassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      return;
    }
    String trimmed = password.trim();
    if (trimmed.length() < 8) {
      throw new BadRequestException("Password must be at least 8 characters long!");
    }
    if (trimmed.length() > 64) {
      throw new BadRequestException("Password must be a maximum of 64 characters long!");
    }
    if (!trimmed.matches(".*[a-z].*")) {
      throw new BadRequestException("Password must contain at least one lowercase letter!");
    }
    if (!trimmed.matches(".*[A-Z].*")) {
      throw new BadRequestException("Password must contain at least one uppercase letter!");
    }
    if (!trimmed.matches(".*\\d.*")) {
      throw new BadRequestException("Password must contain at least one digit!");
    }
    if (!trimmed.matches(".*[^a-zA-Z0-9].*")) {
      throw new BadRequestException("Password must contain at least one special character!");
    }
  }

  // #region Create
  @Transactional
  public UserResponse createUser(UserRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new BadRequestException("Username already exists!");
    }

    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
        && userRepository.existsByEmail(request.getEmail().trim())) {
      throw new BadRequestException("Email already exists!");
    }

    if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
      throw new BadRequestException("Password is required for new users!");
    }

    validatePassword(request.getPassword());

    User user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword().trim()))
        .email(request.getEmail() != null ? request.getEmail().trim() : null)
        .fullName(request.getFullName())
        .isActive(request.getIsActive() != null ? request.getIsActive() : false)
        .build();

    User savedUser = userRepository.save(user);

    // Assign roles if provided, otherwise default to CUSTOMER
    if (request.getRoles() != null && !request.getRoles().isEmpty()) {
      for (String roleStr : request.getRoles()) {
        RoleName roleName = RoleName.valueOf(roleStr);
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleStr));
        UserRole.UserRoleId id = new UserRole.UserRoleId(savedUser.getId(), role.getId());
        UserRole userRole = UserRole.builder()
            .id(id)
            .user(savedUser)
            .role(role)
            .build();
        userRoleRepository.save(userRole);
      }
    } else {
      Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
          .orElseThrow(() -> new ResourceNotFoundException("Default Customer role not found!"));
      UserRole.UserRoleId id = new UserRole.UserRoleId(savedUser.getId(), customerRole.getId());
      UserRole userRole = UserRole.builder()
          .id(id)
          .user(savedUser)
          .role(customerRole)
          .build();
      userRoleRepository.save(userRole);
    }

    return mapToResponse(savedUser);
  }
  // #endregion

  // #region Update
  @Transactional
  public UserResponse updateUser(Integer id, UserRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

    // Username check if changed
    if (!user.getUsername().equalsIgnoreCase(request.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new BadRequestException("Username already exists!");
      }
    }

    // Email check if changed
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
        && !request.getEmail().trim().equalsIgnoreCase(user.getEmail())
        && userRepository.existsByEmail(request.getEmail().trim())) {
      throw new BadRequestException("Email already exists!");
    }

    user.setUsername(request.getUsername());
    if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
      validatePassword(request.getPassword());
      user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
    }
    user.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
    user.setFullName(request.getFullName());
    if (request.getIsActive() != null) {
      user.setIsActive(request.getIsActive());
    }

    User savedUser = userRepository.save(user);

    // Update roles if provided
    if (request.getRoles() != null) {
      // Clear old roles
      List<UserRole> existingRoles = userRoleRepository.findByUserId(id);
      userRoleRepository.deleteAll(existingRoles);

      // Save new roles
      for (String roleStr : request.getRoles()) {
        RoleName roleName = RoleName.valueOf(roleStr);
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleStr));
        UserRole.UserRoleId roleId = new UserRole.UserRoleId(savedUser.getId(), role.getId());
        UserRole userRole = UserRole.builder()
            .id(roleId)
            .user(savedUser)
            .role(role)
            .build();
        userRoleRepository.save(userRole);
      }
    }

    return mapToResponse(savedUser);
  }
  // #endregion

  // #region Update status
  @Transactional
  public UserResponse updateUserStatus(Integer id, UserStatusRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

    user.setIsActive(request.getIsActive());
    return mapToResponse(userRepository.save(user));
  }
  // #endregion
}
