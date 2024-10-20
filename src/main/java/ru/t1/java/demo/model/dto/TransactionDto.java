package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.t1.java.demo.model.Client}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class TransactionDto {

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("is_cancelled")
    private Boolean isCancelled;
}