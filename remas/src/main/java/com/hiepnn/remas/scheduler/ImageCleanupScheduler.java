package com.hiepnn.remas.scheduler;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hiepnn.remas.entity.TemporaryUpload;
import com.hiepnn.remas.feature.upload.repository.TemporaryUploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ImageCleanupScheduler {

    private final TemporaryUploadRepository temporaryUploadRepository;
    private final Cloudinary cloudinary;

    public ImageCleanupScheduler(TemporaryUploadRepository temporaryUploadRepository, Cloudinary cloudinary) {
        this.temporaryUploadRepository = temporaryUploadRepository;
        this.cloudinary = cloudinary;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOrphanedImages() {
        log.info("Starting scheduled cleanup of orphaned images...");
        
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        List<TemporaryUpload> orphanedUploads = temporaryUploadRepository.findByCreatedAtBefore(threshold);

        if (orphanedUploads.isEmpty()) {
            log.info("No orphaned images found to clean up.");
            return;
        }

        log.info("Found {} orphaned image(s) to clean up.", orphanedUploads.size());

        for (TemporaryUpload upload : orphanedUploads) {
            try {
                log.info("Deleting image from Cloudinary: publicId={}", upload.getPublicId());
                Map result = cloudinary.uploader().destroy(upload.getPublicId(), ObjectUtils.emptyMap());
                log.info("Cloudinary destruction result: {}", result);

                temporaryUploadRepository.delete(upload);
                log.info("Removed temporary upload record from database: id={}", upload.getId());
            } catch (IOException e) {
                log.error("Failed to delete orphaned image from Cloudinary: publicId={}, error={}", 
                        upload.getPublicId(), e.getMessage());
            }
        }

        log.info("Orphaned images cleanup completed.");
    }
}
