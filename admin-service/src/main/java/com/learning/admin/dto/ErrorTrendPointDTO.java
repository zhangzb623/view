package com.learning.admin.dto;

import lombok.Data;

@Data
public class ErrorTrendPointDTO {
    private String timeBucket;
    private long count;
}
