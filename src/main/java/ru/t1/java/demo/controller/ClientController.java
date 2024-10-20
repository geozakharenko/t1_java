package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotations.*;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.mapper.ClientMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    @Qualifier("clientServiceImpl")
    private final ClientService clientService;
    private final KafkaProducer kafkaProducer;
    private final ClientMapper clientMapper;
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/client/register")
    public ResponseEntity<Client> register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        Client client = clientService.registerClient(
                clientMapper.toEntityWithId(clientDto)
        );
        log.info("Client registered: {}", client.getId());
        return ResponseEntity.ok().body(client);
    }
}
