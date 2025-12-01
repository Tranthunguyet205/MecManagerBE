package com.example.mecManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionCreateDTO {
    
    @NotBlank(message = "Mã đơn thuốc không được để trống")
    private String prescriptionCode;
    
    // Patient embedded fields
    @NotBlank(message = "Tên bệnh nhân không được để trống")
    @Size(min = 1, max = 100, message = "Tên phải từ 1-100 ký tự")
    private String patientName;
    
    @NotBlank(message = "Số CMND/CCCD không được để trống")
    @Pattern(regexp = "^\\d{9,12}$", message = "Số CMND/CCCD phải từ 9-12 chữ số")
    private String patientNationalId;
    
    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được trong tương lai")
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date patientDob;
    
    @NotNull(message = "Giới tính không được để trống")
    @Min(value = 1, message = "Giới tính không hợp lệ")
    @Max(value = 3, message = "Giới tính không hợp lệ")
    private Integer patientGender;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    private String patientAddress;
    
    @Pattern(regexp = "^$|^\\d{10,11}$", message = "Số điện thoại phải từ 10-11 chữ số")
    private String patientPhone;
    
    // Medical info
    @NotBlank(message = "Chẩn đoán không được để trống")
    private String diagnosis;
    
    private String conclusion;
    
    @NotNull(message = "Loại điều trị không được để trống")
    @Min(value = 0, message = "Loại điều trị phải là 0 hoặc 1")
    @Max(value = 1, message = "Loại điều trị phải là 0 hoặc 1")
    private Integer treatmentType;
    
    @Min(value = 0, message = "Chiều cao không hợp lệ")
    private Float heightCm;
    
    @Min(value = 0, message = "Cân nặng không hợp lệ")
    private Float weightKg;
    
    @NotNull(message = "ID bác sĩ không được để trống")
    private Long doctorId;
    
    private String note;
    
    @NotNull(message = "Danh sách thuốc không được để trống")
    private List<PrescriptionMedicineDTO> medicines;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrescriptionMedicineDTO {
        
        @NotNull(message = "ID thuốc không được để trống")
        private Long medicineId;
        
        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải >= 1")
        private Integer quantity;
        
        @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
        private String usageInstructions;
    }
}
