package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.dto.ExceptionCatcherDto;

@Slf4j
@Aspect
@Component
public class ExceptionCatcherAspect {

    @Value("${t1.kafka.topic.error_trace}")
    private String errorTopic;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public ExceptionCatcherAspect(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @AfterThrowing(pointcut = "within(ru.t1.java.demo..*)",
            throwing = "exception")
    public void catchException(JoinPoint joinPoint, Throwable exception) {
        ExceptionCatcherDto dto = new ExceptionCatcherDto();
            dto.setMethod(joinPoint.getSignature().toShortString());
            dto.setExceptionMessage(exception.getMessage());
            dto.setStackTrace(exception);
            kafkaProducer.sendTo(errorTopic, dto);
    }
}
