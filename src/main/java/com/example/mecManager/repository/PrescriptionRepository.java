package com.example.mecManager.repository;

import com.example.mecManager.model.entity.Prescription;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    List<Prescription> findByPatientNationalId(String nationalId);
    
    List<Prescription> findByPatientName(String patientName);
    
    @Query("SELECT p FROM Prescription p WHERE p.docInfo.id = :doctorId ORDER BY p.createdAt DESC")
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT p FROM Prescription p WHERE p.docInfo.id = :doctorId ORDER BY p.createdAt DESC")
    Page<Prescription> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
    
    @Query("SELECT p FROM Prescription p WHERE p.patientNationalId = :nationalId OR p.patientName LIKE %:patientName% ORDER BY p.createdAt DESC")
    Page<Prescription> searchByPatient(@Param("nationalId") String nationalId, 
                                       @Param("patientName") String patientName, 
                                       Pageable pageable);
}
