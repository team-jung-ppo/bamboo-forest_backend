package org.jungppo.bambooforest.payment.fixture;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.global.client.paymentgateway.toss.dto.TossPaymentSuccessResponse;

public class PaymentResponseFixture {

    public static TossPaymentSuccessResponse createPaymentSuccessResponse(final BatteryItem batteryItem) {
        return new TossPaymentSuccessResponse(
                "2022-11-16",
                "tgen_20240713230839mWKj3",
                "NORMAL",
                "MC4zOTM4OTQzMjc1Nzgw",
                batteryItem.getName(),
                "tgen_docs",
                "KRW",
                "간편결제",
                batteryItem.getPrice(),
                batteryItem.getPrice(),
                "DONE",
                OffsetDateTime.parse("2024-07-13T23:08:39+09:00",
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OffsetDateTime.parse("2024-07-13T23:09:27+09:00",
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                false,
                "86D661B9270E8D8218C30B3DA6C7B08B",
                batteryItem.getPrice().subtract(new BigDecimal("545")),
                new BigDecimal("545"),
                false,
                new BigDecimal("0"),
                0,
                null,
                new TossPaymentSuccessResponse.Card(
                        batteryItem.getPrice(),
                        "24",
                        "21",
                        "53275080****858*",
                        0,
                        "00000000",
                        false,
                        "신용",
                        "개인",
                        "READY",
                        false,
                        null
                ),
                null,
                null,
                null,
                null,
                new TossPaymentSuccessResponse.Receipt(
                        "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tgen_20240713230839mWKj3&ref=PX"
                ),
                new TossPaymentSuccessResponse.Checkout(
                        "https://api.tosspayments.com/v1/payments/tgen_20240713230839mWKj3/checkout"
                ),
                new TossPaymentSuccessResponse.EasyPay(
                        "토스페이",
                        new BigDecimal("0"),
                        new BigDecimal("0")
                ),
                null,
                null,
                null,
                null,
                "KR",
                "ps_mBZ1gQ4YVXbG06dLAyW28l2KPoqN",
                true
        );
    }
}
