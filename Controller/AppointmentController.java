package com.prad.PMS.Controller;

import com.prad.PMS.Entity.Appointment;
import com.prad.PMS.Entity.Doctor;
import com.prad.PMS.Entity.Patient;
import com.prad.PMS.Service.AppointmentService;
import com.prad.PMS.Service.DoctorService;
import com.prad.PMS.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService DoctorService;
    private final PatientService PatientService;


    @PostMapping
    public Appointment create(@RequestBody Appointment appointment) {
        Doctor doctor = DoctorService.getById(appointment.getDoctor().getId());
        Patient patient = PatientService.getById(appointment.getPatient().getId());

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        return appointmentService.create(appointment);
    }


    @GetMapping
    public List<Appointment> getAll() {
        return appointmentService.getAll();
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getByDoctor(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getByPatient(@PathVariable Long patientId) {
        return appointmentService.getByPatient(patientId);
    }

    @GetMapping("/calendar")
    public List<Appointment> getByDateRange(
            @RequestParam String start,
            @RequestParam String end
    ) {
        return appointmentService.getByDateRange(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
    }
}
