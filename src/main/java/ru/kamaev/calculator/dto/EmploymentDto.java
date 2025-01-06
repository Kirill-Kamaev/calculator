package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kamaev.calculator.enums.EmploymentStatus;
import ru.kamaev.calculator.enums.Position;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Место работы")
public class EmploymentDto {
    @Schema(description = "Рабочий статус", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;
    @Schema(description = "ИНН работодателя", example = "123456789")
    private String employerINN;
    @Schema(description = "Зарплата", example = "270000")
    private BigDecimal salary;
    @Schema(description = "Позиция на работе (WORKER, MID_MANAGER, TOP_MANAGER, OWNER)", example = "MID_MANAGER")
    private Position position;
    @Schema(description = "Общий стаж работы (месяцев)", example = "20")
    private Integer workExperienceTotal;
    @Schema(description = "Текущий стаж работы (месяцев)", example = "5")
    private Integer workExperienceCurrent;
}
