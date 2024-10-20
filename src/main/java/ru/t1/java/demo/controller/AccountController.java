package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotations.HandlingResult;
import ru.t1.java.demo.aop.annotations.LogException;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.aop.annotations.Track;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.mapper.AccountMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    @Qualifier("accountServiceImpl")
    private final AccountService accountService;
    private final KafkaProducer kafkaProducer;
    @Value("${t1.kafka.topic.client_accounts}")
    private String topic;

    @LogException
    @Track
    @Metric
    @GetMapping(value = "/parse-accounts")
    @HandlingResult
    public void parseSource() {
        List<AccountDto> accountDtos = accountService.parseJson();
        accountDtos.forEach(dto -> kafkaProducer.sendTo(topic, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/account/register")
    public ResponseEntity<Account> register(@RequestBody AccountDto accountDto) {
        log.info("Registering client: {}", accountDto);
        Account account = accountService.registerAccount(
                AccountMapper.toEntity(accountDto)
        );
        log.info("Account registered: {}", account.getId());
        return ResponseEntity.ok().body(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/account/block-debit/{accountId}")
    public AccountDto blockDebitAccount(@PathVariable Long accountId) {
        return AccountMapper.toDto(accountService.blockDebitAccount(accountId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/account/unblock/{accountId}")
    public ResponseEntity<String> unlockAccount(@PathVariable Long accountId) {
       return accountService.unlockAccount(accountId);
    }
}
