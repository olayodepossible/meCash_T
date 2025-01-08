package com.possible.meCash.service;


import com.possible.task.dto.req.LoginDto;
import com.possible.task.dto.req.UserDto;
import com.possible.task.dto.req.UserInfo;
import com.possible.task.dto.response.ResponseDto;
import com.possible.task.model.AppUser;
import com.possible.task.model.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface UserService {

    ResponseDto<UserInfo> registerUser(UserDto userDto, Role role);
    ResponseDto<UserInfo> userLogin(LoginDto loginDto);
    ResponseDto<Object> userLogout(HttpServletRequest request, HttpServletResponse response);
    ResponseDto nameEnquiry(String accountNumber);
    ResponseDto<AppUser> getAllUsers();
    ResponseDto<String> deleteUserById(Long userId);
}
