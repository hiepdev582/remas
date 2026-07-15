package com.hiepnn.remas.feature.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopItemReportItem {
    private Integer id;
    private String name;
    private Long rentCount;
}
