package com.pms.appointment.feign;

import com.pms.appointment.dto.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {

    @GetMapping("/api/doctors/{id}")
    ResponseEntity<DoctorDto> getDoctorById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/doctors/exists/{id}")
    ResponseEntity<Boolean> checkDoctorExists(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );
}