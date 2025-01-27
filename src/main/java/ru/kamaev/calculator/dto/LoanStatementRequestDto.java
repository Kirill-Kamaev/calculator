package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Schema(description = "Сущность первичной заявки пользователя на кредит (для прескоринга")
@Data
public class LoanStatementRequestDto {
    @Schema(pattern = "r'^([2-9]\\d{4}|[1-9]\\d{5,})$", description = "Сумма кредита >= 20_000", example = "20000")
    private BigDecimal amount;
    @Schema(pattern = "r'^[6-9]$|^[1-9]\\d+$'",description = "Срок кредита в месяцах >= 6", example = "12")
    private Integer term;
    @Schema(pattern = "^[а-яА-Я]{2,30}$", description = "Имя от 2 до 30 букв", example = "Кирилл")
    private String firstName;
    @Schema(pattern = "^[а-яА-Я]{2,30}$",description = "Фамилия от 2 до 30 букв", example = "Камаев")
    private String lastName;
    @Schema(pattern = "^[а-яА-Я]{2,30}$",description = "Отчество", example = "Вадимович")
    private String middleName;
    @Schema(pattern = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$", description = "Электронная почта", format ="email", example = "kamaev@mail.ru")
    private String email;
    @Schema(description = "Дата рождения", format = "date", example = "1996-03-25")
    private LocalDate birthdate;
    @Schema(pattern = "^[0-9]{4}$", description = "Серия паспорта", example = "1234")
    private String passportSeries;
    @Schema(pattern = "^[0-9]{6}$", description = "Номер паспорта", example = "567890")
    private String passportNumber;

}
