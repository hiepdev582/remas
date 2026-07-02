package com.hiepnn.remas.feature.customer.model;

import com.hiepnn.remas.common.constant.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDocumentResponse {
    private Integer id;
    private Integer customerId;
    private DocumentType documentType;
    private String documentNumber;
    private String imageUrl;
}
