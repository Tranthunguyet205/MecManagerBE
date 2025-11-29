package com.example.mecManager.model;

import java.util.List;

public class PrescriptionResDTO {
    private List<PrescriptionDTO> prescriptionDTOList;
    private Long total;

    public List<PrescriptionDTO> getPrescriptionDTOList() { return prescriptionDTOList; }
    public void setPrescriptionDTOList(List<PrescriptionDTO> prescriptionDTOList) { this.prescriptionDTOList = prescriptionDTOList; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
}

