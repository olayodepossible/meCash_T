package com.possible.mecash.service.impl;


import com.possible.mecash.dto.enums.AccountCurrency;
import com.possible.mecash.dto.enums.AccountStatus;
import com.possible.mecash.dto.enums.AccountType;
import com.possible.mecash.dto.req.EmailDto;
import com.possible.mecash.dto.req.LoginDto;
import com.possible.mecash.dto.req.UserDto;
import com.possible.mecash.dto.req.UserInfo;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.exceptiion.ResourceNotFoundException;
import com.possible.mecash.model.Account;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Role;
import com.possible.mecash.repository.AccountRepository;
import com.possible.mecash.repository.RoleRepository;
import com.possible.mecash.repository.UserRepository;
import com.possible.mecash.security.JwtAuthenticationHelper;
import com.possible.mecash.service.UserService;
import com.possible.mecash.utils.AccountUtil;
import com.possible.mecash.utils.EmailServiceUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtAuthenticationHelper jwtHelper;
    private final EmailServiceUtil emailService;
    private static final String SUCCESSFUL_LOGIN = "User login successfully";


    @Override
    public ResponseDto registerUser(UserDto userDto, Role role) {
        boolean userExist = userRepository.existsByEmail(userDto.getEmail());
        if (userExist){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("Attempt to create duplicate user record")
                    .build();
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        AppUser saveAppUser = AppUser.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(encodedPassword)
                .phoneNumber(userDto.getPhoneNumber())
                .age(userDto.getAge())
                .address(userDto.getAddress())
                .email(userDto.getEmail())
                .role(role)
                .isEnable(true) // TODO: validation link can be send to user here
                .build();

        AppUser savedAppUser = userRepository.save(saveAppUser);

        role.setUser(saveAppUser);
        roleRepository.save(role);

        Account userAcct = Account.builder()
                .user(savedAppUser)
                .accountNumber(AccountUtil.generateAccountNumber())
                .accountCurrency(userDto.getCurrency().isEmpty() ? AccountCurrency.NAIRA : AccountCurrency.valueOf(userDto.getCurrency()))
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(0))
                .status(AccountStatus.ACTIVE.name())
                .interestRate(0.5f)
                .build();

        Account savedAcct = accountRepository.save(userAcct);

        List<Account> accountList = new ArrayList<>();
        accountList.add(savedAcct);

        UserInfo userInfo = UserInfo.builder()
                .email(savedAppUser.getEmail())
                .firstName(savedAppUser.getFirstName())
                .lastName(savedAppUser.getLastName())
                .accountNumbers(accountList)
                .address(saveAppUser.getAddress())
                .phoneNumber(savedAppUser.getPhoneNumber())
                .username(savedAppUser.getUsername())
                .token(jwtHelper.generateToken(saveAppUser.getUsername()))
                .build();


        EmailDto mailDto = EmailDto.builder()
                .toAddress(List.of(savedAppUser.getEmail()))
                .content("Dear " + savedAppUser.getFirstName() + ",\n\n Congratulations, you have been successfully registered and your account number is :" + savedAcct.getAccountNumber())
                .subject("Registration Successful")
                .build();
        emailService.sendEmail(mailDto);


        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("User created successfully")
                .data(userInfo)
                .build();
    }

    @Override
    public ResponseDto userLogin(LoginDto loginDto) {
        AppUser appUser = loadUserByUsername(loginDto.getUsername());

        if (!appUser.isEnabled()){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("User Account deactivated")
                    .build();
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), appUser.getPassword())){
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("Invalid user credential")
                    .build();
        }
        String token = jwtHelper.generateToken(appUser.getUsername());

        UserInfo userInfo = UserInfo.builder()
                .email(appUser.getEmail())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .accountNumbers(appUser.getAccountList())
                .address(appUser.getAddress())
                .phoneNumber(appUser.getPhoneNumber())
                .username(appUser.getUsername())
                .token(token)
                .build();

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(SUCCESSFUL_LOGIN)
                .data(userInfo)
                .build();
    }

    @Override
    public ResponseDto userLogout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromHeader(request);


        // If token is not null, invalidate it
        if (token != null) {
            String expiredToken = jwtHelper.setTokenExpirationToPast(token);

            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage("Logout successful")
                    .data(expiredToken)
                    .build();
        } else {
            return ResponseDto.builder()
                    .statusCode(400)
                    .responseMessage("Logout failed: Token not found")
                    .data(token)
                    .build();
        }
    }

    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

/*    @Override
    public ResponseDto nameEnquiry(String accountNumber) {
        // AppUser user = (AppUser) getLoginUser().getData();
        AppUser appUser = userRepository.findByEmail(accountNumber).orElseThrow( () -> new ResourceNotFoundException("Account Number: " + accountNumber + " not found"));

        UserInfo userInfo = UserInfo.builder()
                .email(appUser.getEmail())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .accountNumbers(appUser.getAccountList())
                .address(appUser.getAddress())
                .phoneNumber(appUser.getPhoneNumber())
                .username(appUser.getUsername())
                .build();

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(SUCCESSFUL_LOGIN)
                .data(userInfo)
                .build();
    }
    */

    @Override
    public ResponseDto getAllUsers() {
        List<AppUser> appUsers =  userRepository.findAll();
        List<UserDto> users = appUsers.stream().map(u -> UserDto.builder()
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .age(u.getAge())
                .phoneNumber(u.getPhoneNumber())
                .address(u.getAddress())
                .build()).toList();

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("Users retrieved successfully")
                .data(users)
                .build();

    }

    @Override
    public ResponseDto deleteUserById(Long userId) {

        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
            return ResponseDto.builder()
                    .statusCode(200)
                    .responseMessage("Deleted Successfully")
                    .data(null)
                    .build();

        }
        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage("Error in deletion: User not exit")
                .data(null)
                .build();

    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }


}


