package ru.t1.java.demo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.dto.MetricAckDto;

import java.util.concurrent.atomic.AtomicLong;

@Async
@Slf4j
@Aspect
@Component
public class MetricAspect {

    private static final AtomicLong START_TIME = new AtomicLong();
    @Value("${t1.method-execution-threshold-ms}")
    private Long methodExecutionThreshold;
    @Value("${t1.kafka.topic.metric_trace}")
    private String metricTopic;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public MetricAspect(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
    }

    @Before("@annotation(ru.t1.java.demo.aop.annotations.Metric)")
    public void logExecTime(JoinPoint joinPoint) {
        START_TIME.addAndGet(System.currentTimeMillis());
    }

    @After("@annotation(ru.t1.java.demo.aop.annotations.Metric)")
    public void calculateTime(JoinPoint joinPoint) {
        long methodExecutionTime = System.currentTimeMillis() - START_TIME.get();

        if (methodExecutionTime > methodExecutionThreshold) {
            MetricAckDto ackDto = new MetricAckDto();
            ackDto.setExecutionTime(methodExecutionTime);
            ackDto.setMethodArgs(joinPoint.getArgs());
            ackDto.setMethod(joinPoint.getSignature().toShortString());
            kafkaProducer.sendTo(metricTopic, ackDto);
        }
        START_TIME.set(0L);
    }
}
