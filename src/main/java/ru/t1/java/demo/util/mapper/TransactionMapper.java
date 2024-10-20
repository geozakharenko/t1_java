package ru.t1.java.demo.util.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;

@Component
public class TransactionMapper {

    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .clientId(dto.getClientId())
                .accountId(dto.getAccountId())
                .isCancelled(dto.getIsCancelled())
                .build();
    }

    public static TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .amount(entity.getAmount())
                .clientId(entity.getClientId())
                .accountId(entity.getAccountId())
                .isCancelled(entity.getIsCancelled())
                .build();
    }
}
