package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Schema(description = "График платежей")
@Data
public class PaymentScheduleElementDto {
    @Schema(description = "Номер платежа", example = "1")
    private Integer number;
    @Schema(description = "Дата платежа", example = "2025-03-01")
    private LocalDate date;
    @Schema(description = "Сумма платежа", example = "1833,3")
    private BigDecimal totalPayment;
    @Schema(description = "Сумма процента", example = "166,6")
    private BigDecimal interestPayment;
    @Schema(description = "Сумма налога", example = "16,6")
    private BigDecimal debtPayment;
    @Schema(description = "Оставшейся долг")
    private BigDecimal remainingDebt;

}
