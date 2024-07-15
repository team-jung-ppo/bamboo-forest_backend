package org.jungppo.bambooforest.global.client.paymentgateway.toss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jungppo.bambooforest.global.client.paymentgateway.dto.PaymentResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class TossSuccessResponse implements PaymentResponse {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    @JsonProperty("mId")
    private String mid;
    private String currency;
    private String method;
    private BigDecimal totalAmount;
    private BigDecimal balanceAmount;
    private String status;
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
    private Boolean useEscrow;
    private String lastTransactionKey;
    private BigDecimal suppliedAmount;
    private BigDecimal vat;
    private Boolean cultureExpense;
    private BigDecimal taxFreeAmount;
    private Integer taxExemptionAmount;
    private List<Cancel> cancels;
    private Card card;
    private VirtualAccount virtualAccount;
    private MobilePhone mobilePhone;
    private GiftCertificate giftCertificate;
    private Transfer transfer;
    private Receipt receipt;
    private Checkout checkout;
    private EasyPay easyPay;
    private Failure failure;
    private CashReceipt cashReceipt;
    private List<CashReceipt> cashReceipts;
    private Discount discount;
    private String country;
    private String secret;
    private Boolean isPartialCancelable;

    @Override
    public String getKey() {
        return paymentKey;
    }

    @Override
    public String getProvider() {
        return easyPay.getProvider();
    }

    @Override
    public BigDecimal getAmount() {
        return totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cancel {
        private BigDecimal cancelAmount;
        private String cancelReason;
        private BigDecimal taxFreeAmount;
        private Integer taxExemptionAmount;
        private BigDecimal refundableAmount;
        private BigDecimal easyPayDiscountAmount;
        private OffsetDateTime canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private BigDecimal amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Boolean isInterestFree;
        private String interestPayer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private OffsetDateTime dueDate;
        private String refundStatus;
        private Boolean expired;
        private String settlementStatus;
        private RefundReceiveAccount refundReceiveAccount;
        private String secret;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobilePhone {
        private String customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftCertificate {
        private String approveNo;
        private String settlementStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transfer {
        private String bankCode;
        private String settlementStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Receipt {
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Checkout {
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EasyPay {
        private String provider;
        private BigDecimal amount;
        private BigDecimal discountAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Failure {
        private String code;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CashReceipt {
        private String type;
        private String receiptKey;
        private String issueNumber;
        private String receiptUrl;
        private BigDecimal amount;
        private BigDecimal taxFreeAmount;
        private String orderId;
        private String orderName;
        private String transactionType;
        private String businessNumber;
        private String issueStatus;
        private String customerIdentityNumber;
        private OffsetDateTime requestedAt;
        private Failure failure;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;
    }
}
