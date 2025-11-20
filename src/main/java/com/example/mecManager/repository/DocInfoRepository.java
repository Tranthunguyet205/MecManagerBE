package com.example.mecManager.repository;

import com.example.mecManager.model.DocInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocInfoRepository extends JpaRepository<DocInfo, Long> {

    @Query("select di from DocInfo di where di.user.id = :userId")
    DocInfo findByUserId( @Param("userId") Long userId );
    
    @Query("SELECT d FROM DocInfo d WHERE d.practiceCertificateNo = :practiceCertificateNo")
    DocInfo findByPracticeCertificateNo(@Param("practiceCertificateNo") String practiceCertificateNo);
    
    @Query("SELECT d FROM DocInfo d WHERE d.licenseNo = :licenseNo")
    DocInfo findByLicenseNo(@Param("licenseNo") String licenseNo);

}
