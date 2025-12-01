package com.example.mecManager.repository;

import com.example.mecManager.model.entity.MedicineInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineInfoRepository extends JpaRepository<MedicineInfo, Long> {

    /**
     * Search medicines by medicine code or name (case-insensitive)
     * 
     * @param query    Search query string
     * @param pageable Pagination parameters
     * @return Page of matching medicines
     */
    @Query("SELECT m FROM MedicineInfo m WHERE " +
           "LOWER(m.medicineCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<MedicineInfo> searchMedicines(@Param("query") String query, Pageable pageable);
}
