package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotations.*;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    @Qualifier("clientServiceImpl")
    private final ClientService clientService;
    private final KafkaProducer kafkaProducer;
    @Value("${t1.kafka.topic.client_registration}")
    private String topic;

    @LogException
    @LogExecution
    @Track
    @Metric
    @GetMapping(value = "/parse-clients")
    @HandlingResult
    public void parseSource() {
        List<ClientDto> clientDtos = clientService.parseJson();
        clientDtos.forEach(dto -> kafkaProducer.sendTo(topic, dto));
    }
}
