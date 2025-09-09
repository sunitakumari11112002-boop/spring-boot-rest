package br.com.example.davidarchanjo.service;

import java.util.List;
import java.util.Optional;

import br.com.example.davidarchanjo.model.dto.CustomerDTO;
import br.com.example.davidarchanjo.model.domain.Customer;

public interface CustomerService {

    Long createCustomer(CustomerDTO dto);

    List<CustomerDTO> getAllCustomers();

    Optional<CustomerDTO> getCustomerById(Long customerId);

    Optional<CustomerDTO> updateCustomer(Long customerId, CustomerDTO dto);

    void deleteCustomerById(Long customerId);

    List<CustomerDTO> searchCustomersByName(String name);

    Optional<CustomerDTO> getCustomerByEmail(String email);

    List<CustomerDTO> getCustomersByStatus(Customer.CustomerStatus status);

    void populateTestCustomers();
}
