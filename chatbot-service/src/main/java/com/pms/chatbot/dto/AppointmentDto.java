package com.pms.chatbot.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private LocalDateTime dateTime;
    private String reason;
    private String status;
    private Long patientId;
    private Long doctorId;
}