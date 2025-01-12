package ru.kamaev.calculator.services;

import ru.kamaev.calculator.dto.CreditDto;
import ru.kamaev.calculator.dto.EmploymentDto;
import ru.kamaev.calculator.dto.ScoringDataDto;
import ru.kamaev.calculator.enums.EmploymentStatus;
import ru.kamaev.calculator.enums.Gender;
import ru.kamaev.calculator.enums.MaritalStatus;
import ru.kamaev.calculator.enums.Position;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Properties;

public class ScoringService {
    // Файл с данными
    File propertiesFile = new File("calculator/src/main/resources/data.properties");
    Properties properties = new Properties();

    public CreditDto scoring(ScoringDataDto scoringDataDto) throws IOException {
        CreditDto creditDto = new CreditDto();
        properties.load(new FileReader(propertiesFile));
        BigDecimal rate = calculateRate(scoringDataDto.getIsInsuranceEnabled(), scoringDataDto.getIsSalaryClient());

        // Возраст
        int age = Period.between(scoringDataDto.getBirthdate(), LocalDate.now()).getYears();
        if (age < 18 || age > 65) {
            throw new IllegalArgumentException("Возраст должен быть от 18 до 65 лет");
        }
        // Трудоустройство
        EmploymentDto employmentDto = scoringDataDto.getEmployment();
        if (employmentDto.getEmploymentStatus() == EmploymentStatus.UNEMPLOYED){
            throw new IllegalArgumentException("Необходимо устроиться на работу");
        } else if (employmentDto.getEmploymentStatus() == EmploymentStatus.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(2));
        } else if (employmentDto.getEmploymentStatus() == EmploymentStatus.BUSINESS_OWNER) {
            rate = rate.add(BigDecimal.valueOf(1));
        }

        // Рабочая позиция
        if (employmentDto.getPosition() == Position.MID_MANAGER){
            rate = rate.subtract(BigDecimal.valueOf(2));
        } else if (employmentDto.getPosition() == Position.TOP_MANAGER){
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        // Стаж Работы
        if (employmentDto.getWorkExperienceTotal() < 18 || employmentDto.getWorkExperienceCurrent() < 3){
            throw new IllegalArgumentException("Недостаточный стаж работы");
        }
        // Сумма займа и 24-х зарплат
        BigDecimal maxLoanAmount = employmentDto.getSalary().multiply(BigDecimal.valueOf(24));
        if (scoringDataDto.getAmount().compareTo(maxLoanAmount) > 0){
            throw new IllegalArgumentException("Сумма займа не должна превышать 24-х зарплат");
        }
        // Семейное положение
        if (scoringDataDto.getMaritalStatus() == MaritalStatus.MARRIED){
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if (scoringDataDto.getMaritalStatus() == MaritalStatus.DIVORCED) {
            rate = rate.add(BigDecimal.valueOf(1));
        }
        // Пол и возрастная группа
        if (scoringDataDto.getGender() == Gender.FEMALE && age >= 32 && age <= 60){
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if (scoringDataDto.getGender() == Gender.MALE && age >= 30 && age <= 55) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if (scoringDataDto.getGender() == Gender.NON_BINARY) {
            rate = rate.add(BigDecimal.valueOf(7));
        }

    }

    // Данные для расчета ставки, ежемесячного платежа и итоговой суммы
    public BigDecimal calculateTotalAmount(BigDecimal requestAmount, boolean isInsuranceEnabled) throws IOException {
        properties.load((new FileReader(propertiesFile)));
        BigDecimal insuranceCost = new BigDecimal(properties.getProperty("insurance.cost"));
        if(!isInsuranceEnabled) {
            return requestAmount;
        }else return requestAmount.add(insuranceCost);
    }
    // Данные для расчета ставки, ежемесячного платежа и итоговой суммы
    public BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) throws IOException {
        properties.load((new FileReader(propertiesFile)));
        BigDecimal rate = new BigDecimal(properties.getProperty("rate"));
        BigDecimal insuranceRateDecrease = new BigDecimal(properties.getProperty("insurance.rate.decrease"));
        BigDecimal salaryClientRateDecrease = new BigDecimal(properties.getProperty("salary.client.rate.decrease"));
        if (!isInsuranceEnabled && !isSalaryClient) {
            return rate;
        } else if (isInsuranceEnabled && !isSalaryClient) {
            return rate.subtract(insuranceRateDecrease);
        } else if (!isInsuranceEnabled && isSalaryClient) {
            return rate.subtract(salaryClientRateDecrease);
        } else {
            return rate.subtract(insuranceRateDecrease).subtract(salaryClientRateDecrease);
        }
    }

}
