package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Итоговое предложение после скоринга")
public class CreditDto {
    @Schema(description = "Итоговый платеж", example = "20000")
    private BigDecimal amount;
    @Schema(description = "Срок кредита", example = "12")
    private Integer term;
    @Schema(description = "Сумма ежемесячного платежа", example = "1833,3")
    private BigDecimal monthlyPayment;
    @Schema(description = "Ставка по кредиту", example = "10,0")
    private BigDecimal rate;
    @Schema(description = "Полная сумма кредита", example = "22000.00")
    private BigDecimal psk;
    @Schema(description = "Кредитор переводит зарплату на карту нашего банка", example = "false")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Страховка", example = "false")
    private Boolean isSalaryClient;
    @Schema(description = "График ежемесячных платежей")
    private List<PaymentScheduleElementDto> paymentSchedule;
}
