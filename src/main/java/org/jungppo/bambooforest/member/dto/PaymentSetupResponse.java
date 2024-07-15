package org.jungppo.bambooforest.member.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSetupResponse {
    private UUID paymentId;
    private BigDecimal price;
}
