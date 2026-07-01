package com.hiepnn.remas.feature.customer.model;

import com.hiepnn.remas.common.constant.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDocumentRequest {
    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private String documentNumber;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
