package com.possible.mecash.service;

import com.possible.mecash.dto.enums.AccountCurrency;
import com.possible.mecash.dto.req.LoginDto;
import com.possible.mecash.dto.req.UserDto;
import com.possible.mecash.dto.req.UserInfo;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.model.Account;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Role;
import com.possible.mecash.repository.AccountRepository;
import com.possible.mecash.repository.RoleRepository;
import com.possible.mecash.repository.UserRepository;
import com.possible.mecash.security.JwtAuthenticationHelper;
import com.possible.mecash.service.impl.UserServiceImpl;
import com.possible.mecash.utils.AccountUtil;
import com.possible.mecash.utils.EmailServiceUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {

    @Test
    public void testRegisterUserSuccess() {
        // Mock dependencies
        UserRepository mockUserRepository = mock(UserRepository.class);
        AccountRepository mockAccountRepository = mock(AccountRepository.class);
        RoleRepository mockRoleRepository = mock(RoleRepository.class);
        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        JwtAuthenticationHelper mockJwtHelper = mock(JwtAuthenticationHelper.class);
        EmailServiceUtil mockEmailService = mock(EmailServiceUtil.class);

        // Mock user data
        UserDto userDto = UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .currency(AccountCurrency.NAIRA.name())
                .build();
        Role role = new Role();

        // Mock behavior
        AppUser mockedAppUser = mock(AppUser.class);
        Account mockedAcct = mock(Account.class);
        when(mockUserRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(mockPasswordEncoder.encode(userDto.getPassword())).thenReturn("hashedPassword");
        when(mockJwtHelper.generateToken(anyString())).thenReturn("jwtToken");
        when(mockUserRepository.save(any(AppUser.class))).thenReturn(mockedAppUser);
        when(mockAccountRepository.save(any(Account.class))).thenReturn(mockedAcct);
        when(mockedAppUser.getEmail()).thenReturn(userDto.getEmail());
        when(mockedAcct.getAccountNumber()).thenReturn(AccountUtil.generateAccountNumber());

        // Create the service with mocks
        UserServiceImpl userService = new UserServiceImpl(
                mockUserRepository, mockAccountRepository, mockRoleRepository,
                mockPasswordEncoder, mockJwtHelper, mockEmailService);

        // Call the method
        ResponseDto responseDto = userService.registerUser(userDto, role);

        // Assertions
        assertEquals(200, responseDto.getStatusCode());
        assertEquals("User created successfully", responseDto.getResponseMessage());
        assertNotNull(responseDto.getData());
        assertTrue(responseDto.getData() instanceof UserInfo);

        // Verify interactions with mocks
        verify(mockUserRepository).existsByEmail(userDto.getEmail());
        verify(mockPasswordEncoder).encode(userDto.getPassword());
        // ... (verify other interactions)
    }

    @Test
    public void testRegisterUserExistingEmail() {
        // Mock dependencies
        UserRepository mockUserRepository = mock(UserRepository.class);
        // ... (mock other dependencies)

        // Mock user data
        UserDto userDto = UserDto.builder()
                .email("existing@email.com")
                .password("password123")
                .build();
        Role role = new Role();

        // Mock behavior
        when(mockUserRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Create the service with mocks
        UserServiceImpl userService = new UserServiceImpl(
                mockUserRepository, null, null, null, null, null);

        // Call the method
        ResponseDto responseDto = userService.registerUser(userDto, role);

        // Assertions
        assertEquals(400, responseDto.getStatusCode());
        assertEquals("Attempt to create duplicate user record", responseDto.getResponseMessage());
        assertNull(responseDto.getData());

        // Verify interactions with mocks
        verify(mockUserRepository).existsByEmail(userDto.getEmail());
        // No other interactions expected
    }


    @Test
    public void testUserLoginSuccess() {
        // Mock dependencies
        UserRepository mockUserRepository = mock(UserRepository.class);
        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        JwtAuthenticationHelper mockJwtHelper = mock(JwtAuthenticationHelper.class);

        // Mock user data
        LoginDto loginDto = LoginDto.builder()
                .username("john.doe@example.com")
                .password("password123")
                .build();
        AppUser appUser = new AppUser();
        appUser.setEmail(loginDto.getUsername());
        appUser.setPassword("hashedPassword");

        // Mock behavior
        when(mockUserRepository.findByEmail(loginDto.getUsername())).thenReturn(Optional.of(appUser));
        when(mockPasswordEncoder.matches(loginDto.getPassword(), appUser.getPassword())).thenReturn(true);
        when(mockJwtHelper.generateToken(anyString())).thenReturn("jwtToken");

        // Create the service with mocks
        UserServiceImpl userService = new UserServiceImpl(
                mockUserRepository, null, null, mockPasswordEncoder, mockJwtHelper, null);

        // Call the method
        ResponseDto responseDto = userService.userLogin(loginDto);

        // Assertions
        }
}
