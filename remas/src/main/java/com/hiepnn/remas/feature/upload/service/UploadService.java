package com.hiepnn.remas.feature.upload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hiepnn.remas.entity.TemporaryUpload;
import com.hiepnn.remas.feature.upload.repository.TemporaryUploadRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final Cloudinary cloudinary;
    private final TemporaryUploadRepository temporaryUploadRepository;

    @Value("${app.cloudinary.folder}")
    private String folder;

    @Transactional
    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        // Upload lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", folder
        ));
        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        // Lưu vào bảng tạm
        TemporaryUpload tempUpload = TemporaryUpload.builder()
                .publicId(publicId)
                .imageUrl(url)
                .build();
        temporaryUploadRepository.save(tempUpload);

        // Trả về kết quả
        Map<String, Object> response = new HashMap<>();
        response.put("url", url);
        response.put("publicId", publicId);
        response.put("status", "done");
        response.put("name", file.getOriginalFilename());

        return response;
    }
}
