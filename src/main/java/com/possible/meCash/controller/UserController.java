package com.possible.meCash.controller;


import com.possible.task.dto.req.LoginDto;
import com.possible.task.dto.req.UserDto;
import com.possible.task.dto.req.UserInfo;
import com.possible.task.dto.response.ResponseDto;
import com.possible.task.model.AppUser;
import com.possible.task.model.Role;
import com.possible.task.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



@RestController
@Tag(name = "User Services")
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @Operation(summary = "This method is used to create Admin User.")
    @PostMapping("/admin/register")
    public ResponseEntity<ResponseDto<UserInfo>> registerAdmin(@RequestBody @Valid UserDto admin)
    {
        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");
        ResponseDto<UserInfo> responseDto = userService.registerUser(admin, role);

        if (responseDto.getStatusCode() != 200) {
            return  ResponseEntity.badRequest().body(responseDto);
        }
        return  new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "This method is used to create Users.")
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<UserInfo>> registerCustomer(@RequestBody @Valid UserDto user)
    {
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        ResponseDto<UserInfo> responseDto = userService.registerUser(user, role);

        if (responseDto.getStatusCode() != 200) {
            return  ResponseEntity.badRequest().body(responseDto);
        }
        return  new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "User Login method.")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<UserInfo>> userLogin(@RequestBody LoginDto user)
    {
        ResponseDto<UserInfo> responseDto = userService.userLogin(user);

        if (responseDto.getStatusCode() != 200) {
            return  ResponseEntity.badRequest().body(responseDto);
        }
        return  new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/getAllUser")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<AppUser>> getAllUser(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<String>> deleteUserById(@PathVariable Long userId){
        return new ResponseEntity<>(userService.deleteUserById(userId), HttpStatus.OK);

    }


    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(userService.userLogout(request, response), HttpStatus.OK);
    }
}

