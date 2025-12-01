package com.example.mecManager.dto.request;

import com.example.mecManager.common.enums.UserStatusEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    
    @NotNull(message = "Status không được để trống")
    private UserStatusEnum status;
}
