package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MetricAckDto {
    @JsonProperty("method")
    private String method;

    @JsonProperty("method_args")
    private Object[] methodArgs;

    @JsonProperty("execution_time")
    private long executionTime;
}
