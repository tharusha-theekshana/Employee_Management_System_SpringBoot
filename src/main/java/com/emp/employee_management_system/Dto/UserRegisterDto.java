package com.emp.employee_management_system.Dto;

import com.emp.employee_management_system.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

    private String username;

    private String email;

    private String password;

    private Set<Role> roles;
}
