package com.hiepnn.remas.feature.upload.repository;

import com.hiepnn.remas.entity.TemporaryUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TemporaryUploadRepository extends JpaRepository<TemporaryUpload, Integer> {
    Optional<TemporaryUpload> findByImageUrl(String imageUrl);

    List<TemporaryUpload> findByCreatedAtBefore(LocalDateTime dateTime);
}
