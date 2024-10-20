package ru.t1.java.demo.service;

import org.springframework.http.ResponseEntity;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {
    void registerAccounts(List<Account> accounts);

    List<AccountDto> parseJson();

    Account registerAccount(Account account);

    Account blockDebitAccount(Long accountId);

    ResponseEntity<String> unlockAccount(Long accountId);
}
