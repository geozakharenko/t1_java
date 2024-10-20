package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    void registerTransactions(List<Transaction> transactions);

    List<TransactionDto> parseJson();

    Transaction getTransaction(Long transactionId);

    void cancelTransaction(Long transactionId);
}
