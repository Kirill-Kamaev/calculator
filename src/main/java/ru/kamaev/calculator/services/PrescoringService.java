package ru.kamaev.calculator.services;

import org.springframework.stereotype.Service;
import ru.kamaev.calculator.dto.LoanOfferDto;
import ru.kamaev.calculator.dto.LoanStatementRequestDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Service
public class PrescoringService {
    private final ScoringService scoringService = new ScoringService();
    private final ValidationService validationService = new ValidationService();

    // UUID - уникальный идентификатор
    private final UUID uuid1 = UUID.randomUUID();
    private final UUID uuid2 = UUID.randomUUID();
    private final UUID uuid3 = UUID.randomUUID();
    private final UUID uuid4 = UUID.randomUUID();

    // Метод проверяет валидность запроса
    public List<LoanOfferDto> prescoring (LoanStatementRequestDto loanStatementRequestDto) throws IOException{

        validationService.validateAll(loanStatementRequestDto);
        List<LoanOfferDto> loanOfferDtos = new ArrayList<>();

        // зарплатный клиент со страховкой
        LoanOfferDto loanOfferDto1 = createOffer(loanStatementRequestDto, uuid1, true, true);
        // зарплатный клиент без страховки
        LoanOfferDto loanOfferDto2 = createOffer(loanStatementRequestDto, uuid2, false, true);
        // не зарплатный клиент со страховкой
        LoanOfferDto loanOfferDto3 = createOffer(loanStatementRequestDto, uuid3, true, false);
        // не зарплатный клиент без страховки
        LoanOfferDto loanOfferDto4 = createOffer(loanStatementRequestDto, uuid4, false, false);

        loanOfferDtos.add(loanOfferDto1);
        loanOfferDtos.add(loanOfferDto2);
        loanOfferDtos.add(loanOfferDto3);
        loanOfferDtos.add(loanOfferDto4);

        loanOfferDtos.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());

        return loanOfferDtos;

    }
    // Метод создает предложение
    private LoanOfferDto createOffer(LoanStatementRequestDto loanStatementRequestDto, UUID statementId, Boolean isInsuranceEnabled, Boolean isSalaryClient) throws IOException{
        BigDecimal requestedAmount = loanStatementRequestDto.getAmount();
        BigDecimal totalAmount = scoringService.calculateTotalAmount(requestedAmount, isInsuranceEnabled);
        BigDecimal rate = scoringService.calculateRate(isInsuranceEnabled, isSalaryClient);
        Integer term = loanStatementRequestDto.getTerm();

        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);
        loanOfferDto.setRequestedAmount(loanStatementRequestDto.getAmount());
        loanOfferDto.setTotalAmount(totalAmount);
        loanOfferDto.setTerm(term);
        loanOfferDto.setMonthlyPayment(scoringService.calculateMonthlyPayment(totalAmount, term, rate));
        loanOfferDto.setRate(rate);
        loanOfferDto.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDto.setIsSalaryClient(isSalaryClient);
        return loanOfferDto;

    }
}
