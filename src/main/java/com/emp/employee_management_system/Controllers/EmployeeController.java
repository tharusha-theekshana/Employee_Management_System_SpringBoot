package com.emp.employee_management_system.Controllers;

import com.emp.employee_management_system.Dto.EmployeeDto;
import com.emp.employee_management_system.Jwt.JwtService;
import com.emp.employee_management_system.Services.EmployeeService;
import com.emp.employee_management_system.Utils.Response.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtService jwtService;

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

    //Can access ADMIN users only
    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteEmployee(@PathVariable Long id,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);

            String roles = jwtService.getRoles(jwtToken);
            if (roles.contains("ADMIN")) {
                return employeeService.deleteEmployee(id);
            } else {
                return ResponseHandler.generateResponse(
                        "Access denied !",
                        HttpStatus.FORBIDDEN,
                        null
                );
            }
        } else {
            return ResponseHandler.generateResponse(
                    "Invalid token !",
                    HttpStatus.UNAUTHORIZED,
                    null
            );
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<Object> UpdateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        return employeeService.updateEmployee(id,employeeDto);
    }

    @PostMapping("/uploadProfilePic/{id}")
    ResponseEntity<Object> UploadProfilePic(@PathVariable Long id, @RequestParam("profileImage") MultipartFile profileImage) {
        return employeeService.uploadProfilePicture(id,profileImage);
    }

    @GetMapping("/downloadProfilePic/{id}")
    ResponseEntity<Object> downloadProfilePic(@PathVariable Long id) {
        return employeeService.downloadProfilePicture(id);
    }
}
