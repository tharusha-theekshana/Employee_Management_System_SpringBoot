package com.emp.employee_management_system.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

    private String firstName;

    private String lastName;

    private String email;

    private Date birthday;
}
