package ru.kamaev.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kamaev.calculator.enums.Gender;
import ru.kamaev.calculator.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность заявки пользователя на кредит (для скоринга)")
public class ScoringDataDto {
    @Schema(pattern = "r'^([2-9]\\d{4}|[1-9]\\d{5,})$", description = "Сумма кредита + страховка", example = "120000")
    private BigDecimal amount;
    @Schema(pattern = "r'^[6-9]$|^[1-9]\\d+$'",description = "Срок кредита в месяцах >= 6", example = "12")
    private Integer term;
    @Schema(pattern = "^[а-яА-Я]{2,30}$", description = "Имя от 2 до 30 букв", example = "Кирилл")
    private String firstName;
    @Schema(pattern = "^[а-яА-Я]{2,30}$",description = "Фамилия от 2 до 30 букв", example = "Камаев")
    private String lastName;
    @Schema(pattern = "^[а-яА-Я]{2,30}$",description = "Отчество", example = "Вадимович")
    private String middleName;
    @Schema(description = "Пол (MALE, FEMALE, NON_BINARY)", example = "MALE")
    private Gender gender;
    @Schema(description = "Дата рождения", format = "date", example = "1996-03-25")
    private LocalDate birthdate;
    @Schema(pattern = "^[0-9]{4}$", description = "Серия паспорта", example = "1234")
    private String passportSeries;
    @Schema(pattern = "^[0-9]{6}$", description = "Номер паспорта", example = "567890")
    private String passportNumber;
    @Schema(description = "Дата выдачи паспорта", example = "2014-05-11")
    private LocalDate passportIssueDate;
    @Schema(description = "Регион паспорта (формат ННН-ННН)", example = "123-456")
    private String passportIssueBranch;
    @Schema(description = "Семейное положение (SINGLE, MARRIED, DIVORCED, WIDOW_WIDOWER)", example = "SINGLE")
    private MaritalStatus maritalStatus;
    @Schema(description = "Иждивенцы", example = "0")
    private Integer dependentAmount;
    @Schema(description = "Информация о месте работы", example = "true")
    private EmploymentDto employment;
    @Schema(pattern = "^[0-9]{20}$", description = "Номер счета", example = "47859429038645789327")
    private String accountNumber;
    @Schema(description = "Кредитор переводит зарплату на карту нашего банка", example = "true")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Страховка", example = "true")
    private Boolean isSalaryClient;
}
