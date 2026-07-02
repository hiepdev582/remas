package com.hiepnn.remas.feature.inventory.item.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hiepnn.remas.common.constant.ItemStatus;
import com.hiepnn.remas.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, int id);

    List<Item> findAllByStatus(ItemStatus status, Sort sort);

    List<Item> findAllByStatusAndUserUsername(ItemStatus status, String username, Sort sort);

    List<Item> findAllByCategoryIdAndStatusNot(Integer categoryId, ItemStatus status, Sort sort);
}
