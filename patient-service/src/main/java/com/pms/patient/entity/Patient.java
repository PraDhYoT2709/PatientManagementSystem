package com.pms.patient.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Age cannot be negative")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Disease is required")
    private String disease;

    private String doctorAssigned;

    @PastOrPresent(message = "Admission date can't be in the future")
    private LocalDate admittedDate;

    @PastOrPresent(message = "Discharge date can't be in the future")
    private LocalDate dischargeDate;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    @Column(unique = true)
    private String phone;

    private boolean admitted;

    private String address;

    // Reference to doctor ID (will be validated via doctor-service)
    private Long doctorId;
}