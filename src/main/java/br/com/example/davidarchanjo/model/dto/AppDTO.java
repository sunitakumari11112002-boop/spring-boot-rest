package br.com.example.davidarchanjo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @JsonProperty("lastName")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @JsonProperty("email")
    private String email;

    @Pattern(regexp = "^\\+44[0-9]{10}$", message = "Invalid UK phone number format")
    @NotBlank(message = "Phone number is required")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "Postcode is required")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$", message = "Invalid UK postcode format")
    @JsonProperty("postcode")
    private String postcode;

    @NotBlank(message = "National Insurance Number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{6}[A-Z]$", message = "Invalid UK National Insurance Number format")
    @JsonProperty("nationalInsuranceNumber")
    private String nationalInsuranceNumber;

    @Builder
    public CustomerDTO(String firstName, String lastName, String email, String phoneNumber,
                      LocalDate dateOfBirth, String address, String postcode, String nationalInsuranceNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.postcode = postcode;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
    }
}
