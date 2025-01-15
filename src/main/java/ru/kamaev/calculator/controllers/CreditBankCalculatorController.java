package ru.kamaev.calculator.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kamaev.calculator.dto.LoanStatementRequestDto;
import ru.kamaev.calculator.dto.ScoringDataDto;
import ru.kamaev.calculator.services.PrescoringService;
import ru.kamaev.calculator.services.ScoringService;

import java.io.IOException;

@RestController
@RequestMapping("/calculator")
@Tag(name = "Контроллер банковского калькулятора", description = "Скоринг и прескоринг данных")
public class CreditBankCalculatorController {
    private final PrescoringService prescoringService;
    private final ScoringService scoringService;

    @Autowired
    public CreditBankCalculatorController(PrescoringService prescoringService, ScoringService scoringService) {
        this.prescoringService = prescoringService;
        this.scoringService = scoringService;
    }

    @Operation(
            summary = "Прескоринг",
            description = "По API приходит LoanStatementRequestDto.\n" +
                    "На основании LoanStatementRequestDto происходит прескоринг, создаётся 4 кредитных предложения LoanOfferDto на основании всех возможных комбинаций булевских полей isInsuranceEnabled и isSalaryClient (false-false, false-true, true-false, true-true).\n" +
                    "Логику формирования кредитных предложений можно придумать самому.\n" +
                    "К примеру: в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита, базовая ставка хардкодится в коде через property файл. Например цена страховки 100к (или прогрессивная, в зависимости от запрошенной суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3. Цена зарплатного клиента 0, уменьшает ставку на 1.\n" +
                    "Ответ на API - список из 4х LoanOfferDto от \"худшего\" к \"лучшему\" (чем меньше итоговая ставка, тем лучше)."
    )
    @PostMapping("/offers")
    public ResponseEntity<String> offers(@RequestBody @Parameter(description = "Данные для прескоринга", required = true) LoanStatementRequestDto loanStatement) throws IOException {
        return ResponseEntity.ok(prescoringService.prescoring(loanStatement).toString());
    }
    @Operation(
            summary = "Скоринг",
            description = "По API приходит ScoringDataDto.\n" +
                    "Происходит скоринг данных, высчитывание итоговой ставки(rate), полной стоимости кредита(psk), размер ежемесячного платежа(monthlyPayment), график ежемесячных платежей (List<PaymentScheduleElementDto>). Логику расчета параметров кредита можно найти в интернете, полученный результат сверять с имеющимися в интернете калькуляторами графиков платежей и ПСК.\n" +
                    "Ответ на API - CreditDto, насыщенный всеми рассчитанными параметрами."
    )
    @PostMapping("/calc")
    public ResponseEntity<String> calc(@RequestBody @Parameter(description = "Данные для скоринга", required = true) ScoringDataDto scoringData) throws IOException {
        return ResponseEntity.ok(scoringService.scoring(scoringData).toString());
    }
}
