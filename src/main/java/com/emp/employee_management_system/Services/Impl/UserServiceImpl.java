package com.emp.employee_management_system.Services.Impl;

import com.emp.employee_management_system.Dto.UserLoginDto;
import com.emp.employee_management_system.Dto.UserRegisterDto;
import org.springframework.http.ResponseEntity;

public interface UserServiceImpl {

    ResponseEntity<Object> registerUser(UserRegisterDto user);

    ResponseEntity<Object> login(UserLoginDto user);
}
