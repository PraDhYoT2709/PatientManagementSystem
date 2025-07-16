package com.prad.PMS.Service;

import com.prad.PMS.Entity.Patient;
import com.prad.PMS.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {

    Page<Patient> getAll(Pageable pageable);

    Patient getById(Long id);

    Patient create(Patient patient);

    Patient update(Long id, Patient patient);

    void delete(Long id) throws ResourceNotFoundException;

    List<Patient> searchByDisease(String disease);
}
