package ru.kamaev.calculator.services;

import ru.kamaev.calculator.dto.CreditDto;
import ru.kamaev.calculator.dto.EmploymentDto;
import ru.kamaev.calculator.dto.PaymentScheduleElementDto;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
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

        BigDecimal psk = calculateMonthlyPayment(calculateTotalAmount(scoringDataDto.getAmount(), scoringDataDto.getIsInsuranceEnabled()), scoringDataDto.getTerm(), new BigDecimal(scoringDataDto.getTerm())).multiply(new BigDecimal(scoringDataDto.getTerm()));
        creditDto.setAmount(calculateTotalAmount(scoringDataDto.getAmount(), scoringDataDto.getIsInsuranceEnabled()));
        creditDto.setTerm(scoringDataDto.getTerm());
        creditDto.setMonthlyPayment(calculateMonthlyPayment(calculateTotalAmount(scoringDataDto.getAmount(),scoringDataDto.getIsInsuranceEnabled()), scoringDataDto.getTerm(), rate));
        creditDto.setRate(rate);
        creditDto.setPsk(psk);
        creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        creditDto.setPaymentSchedule(creatingPaymentSchedule(calculateTotalAmount(scoringDataDto.getAmount(),scoringDataDto.getIsInsuranceEnabled()), scoringDataDto.getTerm(), rate, LocalDate.now()));
        return creditDto;
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
    public BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        return (totalAmount.multiply(BigDecimal.ONE.add(rate.divide(new BigDecimal(100),2, RoundingMode.HALF_UP)))).divide(new BigDecimal(term),2, RoundingMode.HALF_UP);
    }
    public List<PaymentScheduleElementDto> creatingPaymentSchedule(
            BigDecimal totalAmount,
            Integer term,
            BigDecimal rate,
            LocalDate startDate
    ) {
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();

        // Ежемесячная процентная ставка
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12),6,RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100),6,RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, term, rate);
        BigDecimal remainingDebt = totalAmount;

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);

            PaymentScheduleElementDto paymentScheduleElement = new PaymentScheduleElementDto();
            paymentScheduleElement.setInterestPayment(interestPayment);
            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(startDate.plusMonths(i - 1));
            paymentScheduleElement.setTotalPayment(monthlyPayment);
            paymentScheduleElement.setDebtPayment(debtPayment);
            paymentScheduleElement.setInterestPayment(remainingDebt.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remainingDebt);
            paymentSchedule.add(paymentScheduleElement);
        }
        return paymentSchedule;
    }

}
