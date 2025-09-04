package com.pms.appointment.feign;

import com.pms.appointment.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @GetMapping("/api/patients/{id}")
    ResponseEntity<PatientDto> getPatientById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/patients/exists/{id}")
    ResponseEntity<Boolean> checkPatientExists(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );
}