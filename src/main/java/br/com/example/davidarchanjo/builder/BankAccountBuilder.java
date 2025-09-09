package br.com.example.davidarchanjo.builder;

import br.com.example.davidarchanjo.model.domain.BankAccount;
import br.com.example.davidarchanjo.model.dto.BankAccountDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankAccountBuilder {

    private final ModelMapper modelMapper;

    public BankAccountDTO build(BankAccount domain) {
        return modelMapper.map(domain, BankAccountDTO.class);
    }

    public BankAccount build(BankAccountDTO dto, BankAccount domain) {
        modelMapper.map(dto, domain);
        return domain;
    }
}
