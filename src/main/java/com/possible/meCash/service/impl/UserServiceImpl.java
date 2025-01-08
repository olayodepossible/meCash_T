package com.possible.meCash.service.impl;


import com.possible.task.dto.enums.AccountStatus;
import com.possible.task.dto.req.*;
import com.possible.task.dto.response.ResponseDto;
import com.possible.task.model.Account;
import com.possible.task.model.AppUser;
import com.possible.task.model.Role;
import com.possible.task.repository.AccountRepository;
import com.possible.task.repository.RoleRepository;
import com.possible.task.repository.UserRepository;
import com.possible.task.security.JwtAuthenticationHelper;
import com.possible.task.service.UserService;
import com.possible.task.utils.AccountUtil;
import com.possible.task.utils.EmailServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
        Optional<AppUser> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()){
            AppUser appUserEntity = optionalUser.get();
            if (!appUserEntity.getAccountList().isEmpty() || role.getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
                return ResponseDto.builder()
                        .statusCode(400)
                        .responseMessage("Attempt to create duplicate user record")
                        .build();
            }

        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        AppUser saveAppUser = AppUser.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(encodedPassword)
                .username(userDto.getUsername())
                .phoneNumber(userDto.getPhoneNumber())
                .isIdentityProof(userDto.isIdentityProof())
                .age(userDto.getAge())
                .address(userDto.getAddress())
                .email(userDto.getEmail())
                .roles(role)
                .isEnable(true)
                .build();

        AppUser savedAppUser = userRepository.save(saveAppUser);

        role.setUser(saveAppUser);
        roleRepository.save(role);

        Account userAcct = Account.builder()
                .user(savedAppUser)
                .accountNumber(AccountUtil.generateAccountNumber())
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
        return userRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

    @Override
    public ResponseDto nameEnquiry(String accountNumber) {
        AppUser appUser = userRepository.findByAccountNumber(accountNumber).orElseThrow( () -> new UsernameNotFoundException("Account Number: " + accountNumber + " not found"));

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

    @Override
    public ResponseDto getAllUsers() {
        List<AppUser> appUsers =  userRepository.findAll();

        return ResponseDto.builder()
                .statusCode(200)
                .responseMessage(SUCCESSFUL_LOGIN)
                .data(appUsers)
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


