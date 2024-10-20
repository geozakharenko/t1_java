package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionErrorsConsumer {

    @Qualifier("transactionServiceImpl")
    private final TransactionService transactionService;
    private final AccountService accountService;

    @KafkaListener(id = "${t1.kafka.consumer.transaction-errors-consumer}",
            topics = "${t1.kafka.topic.client_transaction_errors}",
            containerFactory = "transactionErrorsKafkaListenerFactory")
    public void listener(@Payload List<Long> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            for (Long ids : messageList) {
                Transaction transaction = transactionService.getTransaction(ids);
                if (Objects.nonNull(transaction))
                    accountService.unlockAccount(transaction.getAccountId());
            }
        } finally {
            ack.acknowledge();
        }
        log.debug("Transaction consumer: записи обработаны");
    }
}
