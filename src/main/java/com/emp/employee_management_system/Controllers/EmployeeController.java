package com.emp.employee_management_system.Controllers;

import com.emp.employee_management_system.Dto.EmployeeDto;
import com.emp.employee_management_system.Services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    ResponseEntity<Object> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{email}")
    ResponseEntity<Object> getEmployeeByEmail(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email);
    }

    @PostMapping
    ResponseEntity<Object> saveEmployee(@RequestBody EmployeeDto employeeDto) {
        return employeeService.saveEmployee(employeeDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<Object> deleteEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        return employeeService.updateEmployee(id,employeeDto);
    }

    @PostMapping("/uploadProfilePic/{id}")
    ResponseEntity<Object> deleteEmployee(@PathVariable Long id, @RequestParam("profileImage") MultipartFile profileImage) {
        return employeeService.uploadProfilePicture(id,profileImage);
    }

    @GetMapping("/downloadProfilePic/{id}")
    ResponseEntity<Object> downloadProfilePic(@PathVariable Long id) {
        return employeeService.downloadProfilePicture(id);
    }
}
