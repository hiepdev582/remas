package com.hiepnn.remas.feature.inventory.item.repository;

import com.hiepnn.remas.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {
    List<ItemImage> findByItemId(Integer itemId);

    void deleteByItemId(Integer itemId);
}
