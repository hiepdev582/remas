package com.hiepnn.remas.feature.customer.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {
    @NotBlank(message = "Customer name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0|84|\\+84)[35789][0-9]{8}$", message = "Invalid phone number format")
    private String phone;

    private String identityCard;

    private String driverLicense;

    @Min(value = 0, message = "Trust score must be at least 0")
    @Max(value = 100, message = "Trust score cannot exceed 100")
    private Integer trustScore = 100;

    @Valid
    private List<CustomerDocumentRequest> documents = new ArrayList<>();
}
