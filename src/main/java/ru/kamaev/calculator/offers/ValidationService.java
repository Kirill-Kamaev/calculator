package ru.kamaev.calculator.offers;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;
import ru.kamaev.calculator.dto.EmploymentDto;
import ru.kamaev.calculator.dto.LoanStatementRequestDto;
import ru.kamaev.calculator.dto.ScoringDataDto;
import ru.kamaev.calculator.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    // Паттерны
    private static final Pattern namePattern = Pattern.compile("^[а-яА-Я]{2,30}$");
    private static final Pattern emailPattern = Pattern.compile("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$");
    private static final Pattern passportSeriesPattern = Pattern.compile("^[0-9]{4}$");
    private static final Pattern passportNumberPattern = Pattern.compile("^[0-9]{6}$");
    private static final Pattern PASSPORT_BRANCH_PATTERN = Pattern.compile("^\\d{3}-\\d{3}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{20}$");
    private static final Pattern EMPLOYER_INN_PATTERN = Pattern.compile("^\\d{12}$");
    private static final BigDecimal MIN_SALARY = BigDecimal.valueOf(10_000);
    private static final BigDecimal minAmount = BigDecimal.valueOf(20000);
    private static final int minTerm = 6;
    private static final int minAge = 18;

    // Валидация для прескоринга
    public void validateAll(LoanStatementRequestDto loanStatementRequestDto){
        validateName(loanStatementRequestDto.getFirstName(), "Имя");
        validateName(loanStatementRequestDto.getLastName(), "Фамилия");
        if (loanStatementRequestDto.getMiddleName() != null && !loanStatementRequestDto.getMiddleName().isEmpty()) {
            validateName(loanStatementRequestDto.getMiddleName(), "Отчество");
        }
        validateEmail(loanStatementRequestDto.getEmail());
        validateBirthdate(loanStatementRequestDto.getBirthdate());
        validateAmount(loanStatementRequestDto.getAmount());
        validateTerm(loanStatementRequestDto.getTerm());
        validatePassport(loanStatementRequestDto.getPassportSeries(), loanStatementRequestDto.getPassportNumber());
    }
    // Валидация для скроинга
    public void validateAll(ScoringDataDto scoringDataDto){
        validateAmount(scoringDataDto.getAmount());
        validateTerm(scoringDataDto.getTerm());
        validateName(scoringDataDto.getFirstName(), "Имя");
        validateName(scoringDataDto.getLastName(), "Фамилия");
        if (scoringDataDto.getMiddleName() != null && !scoringDataDto.getMiddleName().isEmpty()) {
            validateName(scoringDataDto.getMiddleName(), "Отчество");
        }
        validateGender(scoringDataDto.getGender());
        validateBirthdate(scoringDataDto.getBirthdate());
        validatePassport(scoringDataDto.getPassportSeries(), scoringDataDto.getPassportNumber());
        validatePassportIssueDate(scoringDataDto.getPassportIssueDate());
        validatePassportIssueBranch(scoringDataDto.getPassportIssueBranch());
        validateMaritalStatus(scoringDataDto.getMaritalStatus());
        validateDependentAmount(scoringDataDto.getDependentAmount());
        validateEmployment(scoringDataDto.getEmployment());
        validateAccountNumber(scoringDataDto.getAccountNumber());
    }

    public void validateName(String name, String fieldName){
        if (name == null || namePattern.matcher(name).matches()){
            throw new IllegalArgumentException(fieldName + " Имя от 2 до 30 букв");
        }
    }
    public void validateEmail(String email){
        if (email == null || emailPattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Email не соответствует формату");
        }
    }
    public void validateBirthdate(LocalDate birthdate){
        if (birthdate == null) {
            throw new IllegalArgumentException("Дата рождения не может быть пустой");
        }
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        if (age < minAge) {
            throw new IllegalArgumentException("Возраст должен быть не менее " + minAge + " лет");
        }
    }
    public void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(minAmount) < 0) {
            throw new IllegalArgumentException("Сумма должна быть не менее " + minAmount);
        }
    }
    public void validateTerm(Integer term){
        if (term == null || term < minTerm) {
            throw new IllegalArgumentException("Срок должен быть не менее " + minTerm + " месяцев");
        }
    }
    public void validatePassport(String passportSeries, String passportNumber){
        if (passportSeries == null || !passportSeriesPattern.matcher(passportSeries).matches()) {
            throw new IllegalArgumentException("Серия паспорта не соответствует формату");
        }
        if (passportNumber == null || !passportNumberPattern.matcher(passportNumber).matches()) {
            throw new IllegalArgumentException("Номер паспорта не соответствует формату");
        }
    }
    public void validateGender(Enum gender){
        if (gender == null) {
            throw new IllegalArgumentException("Пол не может быть пустым");
        }
    }
    public void validatePassportIssueDate(LocalDate passportIssueDate){
        if (passportIssueDate == null) {
            throw new IllegalArgumentException("Дата выдачи паспорта не может быть пустой");
        }
        if (LocalDate.now().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата выдачи паспорта не может быть в будущем");
        }
        if (Period.between(passportIssueDate, LocalDate.now()).getYears() > 20) {
            throw new IllegalArgumentException("Дата выдачи паспорта не может быть более 20 лет назад");
        }
    }
    public void validatePassportIssueBranch(String passportIssueBranch){
        if (passportIssueBranch == null || !PASSPORT_BRANCH_PATTERN.matcher(passportIssueBranch).matches()) {
            throw new IllegalArgumentException("Код подразделения не соответствует формату");
        }
    }
    public void validateMaritalStatus(Enum maritalStatus){
        if (maritalStatus == null) {
            throw new IllegalArgumentException("Семейное положение не может быть пустым");
        }
    }
    public void validateDependentAmount(Integer dependentAmount){
        if (dependentAmount == null || dependentAmount < 0 || dependentAmount > 10) {
            throw new IllegalArgumentException("Количество детей не может быть меньше 0 или больше 10");
        }
    }
    public void validateEmployment(EmploymentDto employment){
        if (employment == null) {
            throw new IllegalArgumentException("Данные о работе не могут быть пустыми");
        }
        if (employment.getEmploymentStatus() == null) {
            throw new IllegalArgumentException("Статус занятости не может быть пустым");
        }
        if (employment.getEmploymentStatus() != EmploymentStatus.UNEMPLOYED) {
            if (employment.getEmployerINN() == null || !EMPLOYER_INN_PATTERN.matcher(employment.getEmployerINN()).matches()) {
                throw new IllegalArgumentException("ИНН работодателя не соответствует формату");
            }
            if (employment.getSalary() == null || employment.getSalary().compareTo(MIN_SALARY) < 0) {
                throw new IllegalArgumentException("Зарплата не может быть меньше " + MIN_SALARY);
            }
            if (employment.getPosition() == null){
                throw new IllegalArgumentException("Должность не может быть пустой");
            }
        }
        if (employment.getWorkExperienceTotal() == null || employment.getWorkExperienceTotal() < 18) {
            throw new IllegalArgumentException("Общий стаж работы не может быть меньше 18 месяцев");
        }
        if (employment.getWorkExperienceCurrent() == null || employment.getWorkExperienceCurrent() < 3) {
            throw new IllegalArgumentException("Текущий стаж работы не может быть меньше 3 месяцев");
        }
        if (employment.getWorkExperienceCurrent() > employment.getWorkExperienceTotal()) {
            throw new IllegalArgumentException("Текущий стаж работы не может быть больше общего");
        }
    }
    public void validateAccountNumber(String accountNumber){
        if (accountNumber == null || !ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new IllegalArgumentException("Номер счета не соответствует формату");
        }
    }
}

