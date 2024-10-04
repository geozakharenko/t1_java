package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotations.HandlingResult;
import ru.t1.java.demo.aop.annotations.LogException;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.aop.annotations.Track;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    @Qualifier("transactionServiceImpl")
    private final TransactionService transactionService;
    private final KafkaProducer kafkaProducer;
    @Value("${t1.kafka.topic.client_transactions}")
    private String topic;

    @LogException
    @Track
    @Metric
    @GetMapping(value = "/parse-transactions")
    @HandlingResult
    public void parseSource() {
        List<TransactionDto> transactionDtos = transactionService.parseJson();
        transactionDtos.forEach(dto -> kafkaProducer.sendTo(topic, dto));
    }
}
