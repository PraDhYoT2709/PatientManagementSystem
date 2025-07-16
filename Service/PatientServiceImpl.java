package com.prad.PMS.Service;

import com.prad.PMS.Entity.Patient;
import com.prad.PMS.Repository.PatientRepository;
import com.prad.PMS.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository repository;

    @Override
    public Page<Patient> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Patient getById(Long id) {
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + id));
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Patient create(Patient patient) {
        return repository.save(patient);
    }

    @Override
    public Patient update(Long id, Patient updated) {
        Patient existing = getById(id);
        updated.setId(existing.getId());
        return repository.save(updated);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Patient> searchByDisease(String disease) {
        return repository.findByDiseaseContainingIgnoreCase(disease);
    }
}
