package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExceptionCatcherDto {

    @JsonProperty("method")
    private String method;

    @JsonProperty("exception_message")
    private String exceptionMessage;

    @JsonProperty("stack_trace")
    private Throwable stackTrace;
}
