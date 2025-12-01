package com.example.mecManager.repository;

import com.example.mecManager.model.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
    
    @Query("SELECT pd FROM PrescriptionDetail pd WHERE pd.prescription.id = :prescriptionId")
    List<PrescriptionDetail> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
    
    @Query("SELECT pd FROM PrescriptionDetail pd WHERE pd.medicineInfo.id = :medicineId")
    List<PrescriptionDetail> findByMedicineId(@Param("medicineId") Long medicineId);
}
