package br.com.example.davidarchanjo.builder;

import java.time.LocalDateTime;

import br.com.example.davidarchanjo.model.domain.Customer;
import br.com.example.davidarchanjo.model.dto.CustomerDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomerBuilder {

    private final ModelMapper modelMapper;

    public Customer build(CustomerDTO dto) {
        Customer model = modelMapper.map(dto, Customer.class);
        model.setStatus(Customer.CustomerStatus.ACTIVE);
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    public CustomerDTO build(Customer domain) {
        return modelMapper.map(domain, CustomerDTO.class);
    }

    public Customer build(CustomerDTO dto, Customer domain) {
        modelMapper.map(dto, domain);
        domain.setUpdatedAt(LocalDateTime.now());
        return domain;
    }
}
