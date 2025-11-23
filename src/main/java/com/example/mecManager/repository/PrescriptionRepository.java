package com.example.mecManager.repository;

import com.example.mecManager.model.Prescription;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Prescription findPrescriptionByPrescriptionCode(String prescriptionCode);

    @NotNull
    @Override
    Optional<Prescription> findById(@NotNull Long aLong);
    
    @Query("SELECT p FROM Prescription p WHERE p.patientProfile.id = :patientId")
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);
    
    @Query("SELECT p FROM Prescription p WHERE p.docInfo.id = :doctorId")
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
}
