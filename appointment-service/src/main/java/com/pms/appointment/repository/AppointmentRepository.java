package com.pms.appointment.repository;

import com.pms.appointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByStatus(Appointment.Status status);
    
    @Query("SELECT a FROM Appointment a WHERE a.dateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByDateTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.dateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByPatientIdAndDateTimeBetween(@Param("patientId") Long patientId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByDoctorIdAndDateTimeBetween(@Param("doctorId") Long doctorId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.dateTime = :dateTime")
    List<Appointment> findByDoctorIdAndDateTime(@Param("doctorId") Long doctorId, 
                                               @Param("dateTime") LocalDateTime dateTime);
}