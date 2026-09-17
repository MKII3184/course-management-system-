package com.mkii.coursemanagementsystem.dto;

import lombok.Data;

@Data
public class CourseRequestDTO {
    private String title;
    private String description;
    private Integer duration;
    private Double price;
}