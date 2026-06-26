package com.smartspend.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDTO {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Monthly income is required")
    private Double monthlyIncome;
    @NotNull(message = "Total balance is required")
    private Double totalBalance;
    private String profileImageUrl;
}
