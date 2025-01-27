package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Schema(description = "Сущность кредитного предложения после прескоринга")
@Data
public class LoanOfferDto {
    @Schema(description = "Идентификатор кредитного предложения", example = "e4f02c8b-1a35-47be-b07c-c69e57d33e70")
    private UUID statementId;
    @Schema(description = "Запрашиваемая сумма кредита", example = "20000")
    private BigDecimal requestedAmount;
    @Schema(description = "Сумма кредита (сумма увеличивается на 100000, если кредитор берёт страховку)", example = "20000")
    private BigDecimal totalAmount;
    @Schema(description = "Количество месяцев", example = "12")
    private Integer term;
    @Schema(description = "Сумма оплаты", example = "1833,3")
    private BigDecimal monthlyPayment;
    @Schema(description = "Ставка по кредиту", example = "10,0")
    private BigDecimal rate;
    @Schema(description = "Кредитор переводит зарплату на карту нашего банка", example = "false")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Страховка", example = "false")
    private Boolean isSalaryClient;
}


