package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;


    @Override
    public void registerTransactions(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }

    @Override
    public List<TransactionDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactions = new TransactionDto[0];
        try {
            transactions = mapper.readValue(new File("src/main/resources/MOCK_DATA3.json"), TransactionDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(transactions);
    }
}
