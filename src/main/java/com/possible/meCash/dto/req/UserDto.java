package com.possible.mecash.dto.req;

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
   private String firstName;
   private String lastName;
   private String email;
   private Integer age;
   @Transient
   private String currency;
   @Transient
   private String password;
   private String address;
   private String phoneNumber;
}
