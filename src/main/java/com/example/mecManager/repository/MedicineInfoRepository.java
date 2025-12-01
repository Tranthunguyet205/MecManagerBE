package com.example.mecManager.repository;

import com.example.mecManager.model.entity.MedicineInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineInfoRepository extends JpaRepository<MedicineInfo, Long> {

}
