package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.exception.AccountNotFoundException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionServiceImpl transactionService;
    private final TransactionRepository transactionRepository;

    @Override
    public void registerAccounts(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }

    @Override
    public Account registerAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account blockDebitAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + accountId));

        if (!account.getAccountType().equals(AccountType.DEBIT)) {
            throw new AccountNotFoundException("No debit account found with this type");
        }

        account.setIsBlocked(true);
        return accountRepository.save(account);
    }

    @Override
    public ResponseEntity<String> unlockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("No account found with ID: " + accountId));

        if (account.getAccountType().equals(AccountType.CREDIT) && account.getIsBlocked()) {
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {

                account.setIsBlocked(false);
                List<Transaction> cancelledTransactions =
                        transactionRepository.findByAccountIdAndIsCancelledTrue(accountId);

                List<Transaction> reproducedTransactions = new ArrayList<>();

                //The crutch
                BigDecimal currentBalance = account.getBalance();
                BigDecimal currentAmount = BigDecimal.ZERO;
                for (Transaction transaction : cancelledTransactions) {
                    currentAmount = currentAmount.add(transaction.getAmount().abs());
                    reproducedTransactions.add(transaction);
                    if (currentAmount.compareTo(currentBalance) > 0) break;
                }

                transactionService.registerTransactions(reproducedTransactions);
                transactionRepository.deleteAll(reproducedTransactions);

                return ResponseEntity.status(HttpStatus.OK).body("");

            } else return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Insufficient funds to unblock account");

        } else if (account.getAccountType().equals(AccountType.DEBIT)) {
            account.setIsBlocked(false);
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body("The account type is incorrect or already available");
    }

    @Override
    public List<AccountDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        AccountDto[] accounts = new AccountDto[0];
        try {
            accounts = mapper.readValue(new File("src/main/resources/MOCK_DATA2.json"), AccountDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(accounts);
    }
}
