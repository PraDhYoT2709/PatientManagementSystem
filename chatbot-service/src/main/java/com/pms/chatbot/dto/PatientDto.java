package com.pms.chatbot.dto;

import lombok.Data;

@Data
public class PatientDto {
    private Long id;
    private String name;
    private int age;
    private String gender;
    private String disease;
    private String email;
    private String phone;
    private boolean admitted;
    private String address;
    private Long doctorId;
}