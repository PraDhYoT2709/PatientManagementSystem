package com.prad.PMS.Controller;

import com.prad.PMS.Entity.Doctor;
import com.prad.PMS.Entity.Patient;
import com.prad.PMS.Exception.ResourceNotFoundException;
import com.prad.PMS.Service.DoctorService;
import com.prad.PMS.Service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    @Autowired
    private DoctorService doctorService;


    private final PatientService patientService;

    // ✅ Get all patients - ADMIN or DOCTOR
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public Page<Patient> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return patientService.getAll(pageable);
    }

    // ✅ Get patient by ID - ADMIN or DOCTOR
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Long id) {
        return patientService.getById(id);
    }

    // ✅ Create a new patient - only ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        if (patient.getDoctor() != null && patient.getDoctor().getId() != null) {
            Doctor doctor = doctorService.getById(patient.getDoctor().getId());
            patient.setDoctor(doctor);
        }
        return ResponseEntity.ok(patientService.create(patient));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient updated) {
        if (updated.getDoctor() != null && updated.getDoctor().getId() != null) {
            Doctor doctor = doctorService.getById(updated.getDoctor().getId());
            updated.setDoctor(doctor);
        }
        return ResponseEntity.ok(patientService.update(id, updated));
    }


    // ✅ Delete patient - only ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) throws ResourceNotFoundException {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Search patients by disease - ADMIN or DOCTOR
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/search")
    public List<Patient> searchByDisease(@RequestParam String disease) {
        return patientService.searchByDisease(disease);
    }

    // ✅ Validation error handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


}
