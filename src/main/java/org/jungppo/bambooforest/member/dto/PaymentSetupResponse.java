package org.jungppo.bambooforest.member.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentSetupResponse {
    private final UUID orderId;
    private final BigDecimal amount;
}
