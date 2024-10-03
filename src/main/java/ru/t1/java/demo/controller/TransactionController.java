package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    @Qualifier("transactionServiceImpl")
    private final TransactionService transactionService;
    private final KafkaClientProducer kafkaClientProducer;
    @Value("${t1.kafka.topic.client_transactions}")
    private String topic;

    @LogException
    @Track
    @GetMapping(value = "/parse-transactions")
    @HandlingResult
    public void parseSource() {
        List<TransactionDto> transactionDtos = transactionService.parseJson();
        transactionDtos.forEach(dto -> kafkaClientProducer.sendTo(topic, dto));
    }

}
