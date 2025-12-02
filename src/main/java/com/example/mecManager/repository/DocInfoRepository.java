package com.example.mecManager.repository;

import com.example.mecManager.model.entity.DocInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocInfoRepository extends JpaRepository<DocInfo, Long> {

    @Query("select di from DocInfo di where di.user.id = :userId")
    DocInfo findByUserId( @Param("userId") Long userId );
    
    @Query("SELECT d FROM DocInfo d WHERE LOWER(d.practiceCertificateNo) LIKE LOWER(CONCAT('%', :practiceCertificateNo, '%'))")
    List<DocInfo> findByPracticeCertificateNo(@Param("practiceCertificateNo") String practiceCertificateNo);
    
    @Query("SELECT d FROM DocInfo d WHERE LOWER(d.licenseNo) LIKE LOWER(CONCAT('%', :licenseNo, '%'))")
    List<DocInfo> findByLicenseNo(@Param("licenseNo") String licenseNo);

    @Query("SELECT d FROM DocInfo d WHERE LOWER(d.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(d.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<DocInfo> findBySearchTerm(@Param("searchTerm") String searchTerm);

}
