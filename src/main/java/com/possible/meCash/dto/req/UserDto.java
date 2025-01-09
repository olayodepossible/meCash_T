package com.possible.mecash.dto.req;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
   @NotBlank(message = "Invalid firstName: must not be empty")
   @NotNull(message = "Invalid firstName:  must not be NULL")
   @Size(min = 3, max = 30, message = "Invalid firstName: Must be of 3 - 30 characters")
   private String firstName;

   @NotBlank(message = "Invalid lastName: must not be empty")
   @NotNull(message = "Invalid lastName:  must not be NULL")
   @Size(min = 3, max = 30, message = "Invalid lastName: Must be of 3 - 30 characters")
   private String lastName;
   @Email(message = "Invalid email")
   private String email;

   @Min(value = 16, message = "Invalid Age: must be 16 years or more to register")
   @Max(value = 100, message = "Invalid Age: must not pass 100years")
   private Integer age;
   @Transient
   private String currency;
   @Transient
   private String password;
   private String address;
   private String phoneNumber;
}
