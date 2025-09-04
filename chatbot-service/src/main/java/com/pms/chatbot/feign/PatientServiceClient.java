package com.pms.chatbot.feign;

import com.pms.chatbot.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @GetMapping("/api/patients/{id}")
    ResponseEntity<PatientDto> getPatientById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/patients")
    ResponseEntity<List<PatientDto>> getAllPatients(
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/patients/search?disease={disease}")
    ResponseEntity<List<PatientDto>> searchPatientsByDisease(
            @PathVariable("disease") String disease,
            @RequestHeader("Authorization") String authorization
    );
}