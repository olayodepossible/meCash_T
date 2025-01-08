package com.possible.mecash.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
   private String firstName;
   private String lastName;
   private String email;
   private String username;
   private Integer age;
   private String password;
   private String address;
   private String phoneNumber;
   private boolean identityProof;
}
