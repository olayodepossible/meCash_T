package com.possible.mecash.controller;



import com.possible.mecash.dto.req.LoginDto;
import com.possible.mecash.dto.req.UserDto;
import com.possible.mecash.dto.req.UserInfo;
import com.possible.mecash.dto.response.ResponseDto;
import com.possible.mecash.model.AppUser;
import com.possible.mecash.model.Role;
import com.possible.mecash.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;





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

