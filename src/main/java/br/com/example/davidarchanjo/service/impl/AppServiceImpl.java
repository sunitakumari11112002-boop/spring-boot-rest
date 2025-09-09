package br.com.example.davidarchanjo.service.impl;

import br.com.example.davidarchanjo.builder.CustomerBuilder;
import br.com.example.davidarchanjo.exception.CustomerNotFoundException;
import br.com.example.davidarchanjo.model.domain.Customer;
import br.com.example.davidarchanjo.model.dto.CustomerDTO;
import br.com.example.davidarchanjo.repository.CustomerRepository;
import br.com.example.davidarchanjo.service.CustomerService;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerBuilder builder;

    @Override
    public Long createCustomer(CustomerDTO dto) {
        // Check if email or NI number already exists
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Customer with this email already exists");
        }
        if (repository.existsByNationalInsuranceNumber(dto.getNationalInsuranceNumber())) {
            throw new IllegalArgumentException("Customer with this National Insurance Number already exists");
        }

        return Stream.of(dto)
            .map(builder::build)
            .map(repository::save)
            .map(Customer::getCustomerId)
            .findFirst()
            .get();
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return repository.findAll()
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(Long customerId) {
        return Optional.of(repository.findById(customerId)
            .map(builder::build)
            .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found for id '%s'", customerId))));
    }

    @Transactional
    @Override
    public Optional<CustomerDTO> updateCustomer(Long customerId, CustomerDTO dto) {
        return Optional.of(repository.findById(customerId)
            .map(model -> builder.build(dto, model))
            .map(repository::save)
            .map(builder::build)
            .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found for id '%s'", customerId))));
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException(String.format("No customer found for id '%s'", customerId));
        }
        repository.deleteById(customerId);
    }

    @Override
    public List<CustomerDTO> searchCustomersByName(String name) {
        return repository.findByNameContaining(name)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerByEmail(String email) {
        return repository.findByEmail(email)
            .map(builder::build);
    }

    @Override
    public List<CustomerDTO> getCustomersByStatus(Customer.CustomerStatus status) {
        return repository.findByStatus(status)
            .stream()
            .map(builder::build)
            .collect(Collectors.toList());
    }

    @Override
    public void populateTestCustomers() {
        val faker = new Faker();
        IntStream.range(0, 50).forEach(i -> {
            Customer customer = Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phoneNumber("+44" + faker.number().digits(10))
                .dateOfBirth(faker.date().birthday(18, 80).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate())
                .address(faker.address().fullAddress())
                .postcode(generateUKPostcode(faker))
                .nationalInsuranceNumber(generateNINumber(faker))
                .status(Customer.CustomerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

            repository.save(customer);
        });
    }

    private String generateUKPostcode(Faker faker) {
        String[] areas = {"SW", "NW", "SE", "NE", "E", "W", "N", "S", "EC", "WC"};
        String area = areas[faker.random().nextInt(areas.length)];
        return area + faker.number().numberBetween(1, 20) + " " +
               faker.number().numberBetween(1, 9) +
               faker.regexify("[A-Z]{2}");
    }

    private String generateNINumber(Faker faker) {
        return faker.regexify("[A-Z]{2}") +
               faker.number().digits(6) +
               faker.regexify("[A-Z]");
    }
}
