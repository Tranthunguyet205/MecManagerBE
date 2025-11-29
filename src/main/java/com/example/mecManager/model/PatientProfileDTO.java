package com.example.mecManager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Date;

/**
 * Data Transfer Object for Patient Profile
 * Used for API requests and responses to prevent direct entity exposure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfileDTO {

    private Long id;

    @NotBlank(message = "Tên bệnh nhân không được để trống")
    @Size(max = 100, message = "Tên bệnh nhân không được vượt quá 100 ký tự")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh phải là ngày trong quá khứ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dob;

    @Min(value = 0, message = "Giới tính không hợp lệ (0: Nam, 1: Nữ, 2: Khác)")
    @Max(value = 2, message = "Giới tính không hợp lệ (0: Nam, 1: Nữ, 2: Khác)")
    private Integer gender;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;

    @NotNull(message = "Loại điều trị không được để trống")
    @Min(value = 0, message = "Loại điều trị không hợp lệ (0: Ngoại trú, 1: Nội trú)")
    @Max(value = 1, message = "Loại điều trị không hợp lệ (0: Ngoại trú, 1: Nội trú)")
    private Integer treatmentType; // 0: Ngoại trú (outpatient), 1: Nội trú (inpatient)

    @Positive(message = "Chiều cao phải là số dương")
    private Float heightCm;

    @Positive(message = "Cân nặng phải là số dương")
    private Float weightKg;

    @NotBlank(message = "Chẩn đoán không được để trống")
    @Size(max = 500, message = "Chẩn đoán không được vượt quá 500 ký tự")
    private String diagnosis; // Chẩn đoán bệnh

    @Size(max = 500, message = "Kết luận không được vượt quá 500 ký tự")
    private String conclusion; // Kết luận bệnh

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date updatedAt;

    private String createdByUserName; // Username of the creator
    private String updatedByUserName; // Username of the last updater
}
