package br.com.example.davidarchanjo.builder;

import br.com.example.davidarchanjo.model.domain.Transaction;
import br.com.example.davidarchanjo.model.dto.TransactionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransactionBuilder {

    private final ModelMapper modelMapper;

    public TransactionDTO build(Transaction domain) {
        return modelMapper.map(domain, TransactionDTO.class);
    }

    public Transaction build(TransactionDTO dto, Transaction domain) {
        modelMapper.map(dto, domain);
        return domain;
    }
}
