package com.possible.mecash.dto.req;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserReq {

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @Email(message = "Invalid email format")
        private String email;

        @Pattern(regexp = "^+//d{10}$", message = "Input correct Phone number")
        private String phoneNumber;
}

