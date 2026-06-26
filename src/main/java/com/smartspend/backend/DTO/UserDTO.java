package com.smartspend.backend.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//This dto is returning to the client
//It does not contains any sensitive data or data that is not relevant to front end
public class UserDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Monthly income is required")
    private Double monthlyIncome;
    private Double totalBalance;
    private String profileImageUrl;
}
