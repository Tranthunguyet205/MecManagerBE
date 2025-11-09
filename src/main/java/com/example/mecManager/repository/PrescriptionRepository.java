package com.example.mecManager.repository;

import com.example.mecManager.model.Prescription;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Prescription findPrescriptionByPrescriptionCode(String prescriptionCode);

    @NotNull
    @Override
    Optional<Prescription> findById(@NotNull Long aLong);
}
