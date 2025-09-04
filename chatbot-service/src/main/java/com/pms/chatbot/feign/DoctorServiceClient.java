package com.pms.chatbot.feign;

import com.pms.chatbot.dto.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {

    @GetMapping("/api/doctors/{id}")
    ResponseEntity<DoctorDto> getDoctorById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/doctors")
    ResponseEntity<List<DoctorDto>> getAllDoctors(
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/doctors/specialty/{specialty}")
    ResponseEntity<List<DoctorDto>> getDoctorsBySpecialty(
            @PathVariable("specialty") String specialty,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/doctors/available")
    ResponseEntity<List<DoctorDto>> getAvailableDoctors(
            @RequestHeader("Authorization") String authorization
    );
}