package org.jungppo.bambooforest.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSetupRequest {

    @NotBlank(message = "Battery item name cannot be blank")
    @Size(max = 15, message = "Battery item name must be less than or equal to 15 characters")
    private String batteryItemName;
}
