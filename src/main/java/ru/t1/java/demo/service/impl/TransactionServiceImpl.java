package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.exception.AccountNotFoundException;
import ru.t1.java.demo.exception.TransactionNotFoundException;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;

    @Value("${t1.kafka.topic.client_transaction_errors}")
    private String transactionErrorTopic;

    @Override
    public void registerTransactions(List<Transaction> transactions) {
        transactionRepository.saveAllAndFlush(transactions);
        for (Transaction transaction : transactions) {
            Account account =
                    accountRepository.findById(transaction.getAccountId())
                            .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + transaction.getAccountId()));

            if (account.getIsBlocked()) {
                transaction.setIsCancelled(true);
                kafkaProducer.sendTo(transactionErrorTopic, transaction.getId());
                continue;
            }

            account.setBalance(account.getBalance().add(transaction.getAmount()));
            transaction.setIsCancelled(false);

            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                account.setIsBlocked(true);
            }
        }
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Транзакция с id %s не найдена", transactionId)));
    }

    @Override
    public void cancelTransaction(Long transactionId) {
        Transaction cancelledTransaction =
                transactionRepository.findById(transactionId).orElseThrow(() ->
                        new AccountNotFoundException("No transaction found with ID: " + transactionId));

        Account accountWithCancelledTransaction = accountRepository.findById(cancelledTransaction.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + cancelledTransaction.getAccountId()));

        if (!cancelledTransaction.getIsCancelled()) {
            accountWithCancelledTransaction.setBalance(
                    accountWithCancelledTransaction.getBalance().add(cancelledTransaction.getAmount().negate()));
        }

        transactionRepository.deleteById(transactionId);
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

    @Scheduled(fixedDelayString = "${t1.schedule.transaction.resend.period}")
    public void retryTransaction() {
        List<Transaction> transactionsForRetry = transactionRepository.findByIsCancelledTrue();
        Map<Long, Long> individualAccountBasedTransactions = new HashMap<>();

        for (Transaction transaction : transactionsForRetry) {
            individualAccountBasedTransactions.put(transaction.getAccountId(), transaction.getId());
        }

        for (Map.Entry<Long, Long> entry : individualAccountBasedTransactions.entrySet()) {
            kafkaProducer.sendTo(transactionErrorTopic, entry.getValue());
        }
    }
}
