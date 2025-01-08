package com.possible.meCash.dto.req;



import com.possible.task.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String username;
    private List<Account> accountNumbers;
    private String token;
}
