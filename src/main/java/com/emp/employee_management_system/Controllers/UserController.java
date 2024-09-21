package com.emp.employee_management_system.Controllers;

import com.emp.employee_management_system.Dto.UserDto;
import com.emp.employee_management_system.Dto.UserLoginDto;
import com.emp.employee_management_system.Dto.UserRegisterDto;
import com.emp.employee_management_system.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRegisterDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> register(@RequestBody UserLoginDto userDto) {
        return userService.login(userDto);
    }
}
