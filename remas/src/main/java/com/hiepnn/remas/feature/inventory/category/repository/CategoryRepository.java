package com.hiepnn.remas.feature.inventory.category.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hiepnn.remas.common.constant.CategoryStatus;
import com.hiepnn.remas.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, int id);

  List<Category> findAllByStatus(CategoryStatus status, Sort sort);
}
