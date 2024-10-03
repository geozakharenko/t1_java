package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public void registerAccounts(List<Account> accounts) {
        repository.saveAll(accounts);
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
