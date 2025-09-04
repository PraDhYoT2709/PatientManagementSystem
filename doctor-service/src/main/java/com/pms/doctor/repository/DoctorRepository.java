package com.pms.doctor.repository;

import com.pms.doctor.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByEmail(String email);
    
    Optional<Doctor> findByPhone(String phone);
    
    List<Doctor> findBySpecialty(String specialty);
    
    List<Doctor> findByDepartment(String department);
    
    List<Doctor> findByAvailableTrue();
    
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Doctor> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))")
    Page<Doctor> findBySpecialtyContainingIgnoreCase(@Param("specialty") String specialty, Pageable pageable);
}