package com.pms.chatbot.feign;

import com.pms.chatbot.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "appointment-service")
public interface AppointmentServiceClient {

    @GetMapping("/api/appointments/patient/{patientId}")
    ResponseEntity<List<AppointmentDto>> getAppointmentsByPatientId(
            @PathVariable("patientId") Long patientId,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/appointments/doctor/{doctorId}")
    ResponseEntity<List<AppointmentDto>> getAppointmentsByDoctorId(
            @PathVariable("doctorId") Long doctorId,
            @RequestHeader("Authorization") String authorization
    );
}