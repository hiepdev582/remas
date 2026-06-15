package com.hiepnn.remas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hiepnn.remas.common.constant.RoleName;
import com.hiepnn.remas.entity.Role;
import com.hiepnn.remas.feature.auth.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) throws Exception {
    if (roleRepository.count() == 0) {
      Role superAdmin = Role.builder().name(RoleName.SUPER_ADMIN).build();
      Role admin = Role.builder().name(RoleName.ADMIN).build();
      Role customer = Role.builder().name(RoleName.CUSTOMER).build();

      roleRepository.save(superAdmin);
      roleRepository.save(admin);
      roleRepository.save(customer);

      System.out.println("Data initialization completed");
    } else {
      System.out.println("Data initialization already completed");
    }
  }
}
