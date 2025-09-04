package com.pms.chatbot.dto;

import lombok.Data;

@Data
public class DoctorDto {
    private Long id;
    private String name;
    private String specialty;
    private String email;
    private String phone;
    private String qualification;
    private Integer experience;
    private String department;
    private String consultationFee;
    private boolean available;
}