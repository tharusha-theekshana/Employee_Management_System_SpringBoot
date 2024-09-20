package com.emp.employee_management_system.Services;

import com.emp.employee_management_system.Dto.EmployeeDto;
import com.emp.employee_management_system.Entity.Employee;
import com.emp.employee_management_system.Repository.EmployeeRepo;
import com.emp.employee_management_system.Services.Impl.EmployeeServiceImpl;
import com.emp.employee_management_system.Utils.Response.ResponseHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements EmployeeServiceImpl {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<Object> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepo.findAll();

            if (employees.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "No employees found",
                        HttpStatus.NOT_FOUND,
                        employees
                );
            }

            return ResponseHandler.generateResponse(
                    "Employees retrieved successfully!",
                    HttpStatus.OK,
                    employees
            );

        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while retrieving employees: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> getEmployeeByEmail(String email) {
        try {
            Optional<Employee> employee = employeeRepo.findEmployeeByEmail(email);

            if (employee.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Employee not found with email: " + email,
                        HttpStatus.NOT_FOUND,
                        ""
                );
            }

            return ResponseHandler.generateResponse(
                    "Employee retrieved successfully!",
                    HttpStatus.OK,
                    employee
            );

        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while retrieving the employee: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> saveEmployee(EmployeeDto employeeDto) {
        try {
            Employee employee = modelMapper.map(employeeDto, Employee.class);

            if (employee.getBirthday() != null) {
                long ageInDays = calculateAgeInDays(employee.getBirthday());
                employee.setCurrentAgeInDays(ageInDays);
            }

            Employee savedEmployee = employeeRepo.save(employee);

            return ResponseHandler.generateResponse(
                    "Employee saved successfully!",
                    HttpStatus.CREATED,
                    savedEmployee
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseHandler.generateResponse(
                    "Email already exists.",
                    HttpStatus.BAD_REQUEST,
                    null
            );
        }
        catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while saving the employee: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> deleteEmployee(Long id) {
        try {
            Employee employee = employeeRepo.findById(id).orElse(null);

            if (employee == null) {
                return ResponseHandler.generateResponse(
                        "Employee not found with ID: " + id,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            employeeRepo.delete(employee);

            return ResponseHandler.generateResponse(
                    "Employee deleted successfully!",
                    HttpStatus.OK,
                    null
            );
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while deleting the employee: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> updateEmployee(Long id, EmployeeDto employeeDto) {
        try {
            Optional<Employee> existingEmployee = employeeRepo.findById(id);

            if (existingEmployee.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Employee not found with ID: " + id,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            existingEmployee.get().setFirstName(employeeDto.getFirstName());
            existingEmployee.get().setLastName(employeeDto.getLastName());
            existingEmployee.get().setEmail(employeeDto.getEmail());
            existingEmployee.get().setBirthday(employeeDto.getBirthday());


            if (existingEmployee.get().getBirthday() != null) {
                long ageInDays = calculateAgeInDays(existingEmployee.get().getBirthday());
                existingEmployee.get().setCurrentAgeInDays(ageInDays);
            }

            Employee updatedEmployee = employeeRepo.save(existingEmployee.get());

            return ResponseHandler.generateResponse(
                    "Employee updated successfully!",
                    HttpStatus.OK,
                    updatedEmployee
            );
        } catch (DataIntegrityViolationException ex) {
            return ResponseHandler.generateResponse(
                    "Email already exists. Please use a different email address.",
                    HttpStatus.CONFLICT,
                    null
            );
        } catch (Exception ex) {
            return ResponseHandler.generateResponse(
                    "An error occurred while updating the employee: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }


    //Age calculation in days method
    private long calculateAgeInDays(Date birthday) {
        LocalDate birthDate = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getDays() +
                Period.between(birthDate, currentDate).getYears() * 365L;
    }


}
