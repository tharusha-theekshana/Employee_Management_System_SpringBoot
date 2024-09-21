package com.emp.employee_management_system.Services.Impl;

import com.emp.employee_management_system.Dto.EmployeeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface EmployeeServiceImpl {

    ResponseEntity<Object> getAllEmployees();

    ResponseEntity<Object> getEmployeeByEmail(String email);

    ResponseEntity<Object> saveEmployee(EmployeeDto employeeDto);

    ResponseEntity<Object> deleteEmployee(Long id);

    ResponseEntity<Object> updateEmployee(Long id,EmployeeDto employeeDto);

    ResponseEntity<Object> uploadProfilePicture(Long id,MultipartFile file);

    ResponseEntity<Object> downloadProfilePicture(Long id) throws MalformedURLException;
}
