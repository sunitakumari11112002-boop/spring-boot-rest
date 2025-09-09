package br.com.ukbank.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Request DTO for customer updates
 * Implements validation and data transfer patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+44[0-9]{10}$", message = "Invalid UK phone number format (+44xxxxxxxxxx)")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String addressLine;

    @NotBlank(message = "Postcode is required")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$", message = "Invalid UK postcode format")
    private String postcode;
}
