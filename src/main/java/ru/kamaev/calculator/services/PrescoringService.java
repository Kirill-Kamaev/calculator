package ru.kamaev.calculator.services;

import org.springframework.stereotype.Service;
import ru.kamaev.calculator.dto.LoanOfferDto;
import ru.kamaev.calculator.dto.LoanStatementRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PrescoringService {



    private final UUID uuid1 = UUID.randomUUID();
    private final UUID uuid2 = UUID.randomUUID();
    private final UUID uuid3 = UUID.randomUUID();
    private final UUID uuid4 = UUID.randomUUID();

    public List<LoanOfferDto> prescoring (LoanStatementRequestDto loanStatementRequestDto) throws IOException{


    }
}
