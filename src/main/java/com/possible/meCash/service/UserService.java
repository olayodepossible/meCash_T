package com.possible.mecash.service;


import com.possible.mecash.dto.req.LoginDto;
import com.possible.mecash.dto.req.UserDto;
import com.possible.mecash.dto.req.UserInfo;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    ResponseDto<UserInfo> registerUser(UserDto userDto, Role role);
    ResponseDto<UserInfo> userLogin(LoginDto loginDto);
    ResponseDto<Object> userLogout(HttpServletRequest request, HttpServletResponse response);
    ResponseDto nameEnquiry(String accountNumber);
    ResponseDto<AppUser> getAllUsers();
    ResponseDto<String> deleteUserById(Long userId);
}
