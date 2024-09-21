package com.emp.employee_management_system.Services;

import com.emp.employee_management_system.Dto.EmployeeDto;
import com.emp.employee_management_system.Entity.Employee;
import com.emp.employee_management_system.Repository.EmployeeRepo;
import com.emp.employee_management_system.Services.Impl.EmployeeServiceImpl;
import com.emp.employee_management_system.Utils.Response.ResponseHandler;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
                    "Something went wrong ! " + e.getMessage(),
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
                        null
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
        } catch (DataIntegrityViolationException e) {
            return ResponseHandler.generateResponse(
                    "Email already exists. Please use a different email address.",
                    HttpStatus.CONFLICT,
                    null
            );
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while updating the employee: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> uploadProfilePicture(Long id, MultipartFile file) {
        try{
            Optional<Employee> existingEmployee = employeeRepo.findById(id);

            if (existingEmployee.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Employee not found with ID: " + id,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            if (file.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "No file uploaded",
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            boolean isImage = checkFileIsImage(file.getOriginalFilename());
            if(!isImage){
                return ResponseHandler.generateResponse(
                        "Only JPG, JPEG, and PNG files are allowed",
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            Path uploadsDir = Paths.get(System.getProperty("user.dir"), "uploads");
            System.out.println("Uploads directory: " + uploadsDir.toAbsolutePath()); // Print uploads directory
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir); // Create the uploads directory if it doesn't exist
            }

            if (existingEmployee.get().getProfilePicturePath() != null) {
                File oldFile = new File(existingEmployee.get().getProfilePicturePath());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

            // Create a new file name
            String newFileName = id + "_" + existingEmployee.get().getFirstName() + "_" + existingEmployee.get().getLastName() + fileExtension;

            // Save the new profile picture
            String filePath = uploadsDir.resolve(newFileName).toString(); // Use resolved path
            File dest = new File(filePath);
            file.transferTo(dest);

            // Update employee's profile picture path
            existingEmployee.get().setProfilePicturePath(filePath);
            employeeRepo.save(existingEmployee.get());

            return ResponseHandler.generateResponse(
                    "Profile picture uploaded successfully!",
                    HttpStatus.OK,
                    existingEmployee.get()
            );
        }catch (Exception e){
            return ResponseHandler.generateResponse(
                    "An error occurred while uploading profile picture : " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> downloadProfilePicture(Long id) {
        try {
            Optional<Employee> existingEmployee = employeeRepo.findById(id);

            if (existingEmployee.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Employee not found with ID: " + id,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            String profilePicturePath = existingEmployee.get().getProfilePicturePath();

            if (profilePicturePath == null || profilePicturePath.isEmpty()) {
                return ResponseHandler.generateResponse(
                        "No profile picture found for employee with ID: " + id,
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            File file = new File(profilePicturePath);
            if (!file.exists()) {
                return ResponseHandler.generateResponse(
                        "Profile picture file not found on server",
                        HttpStatus.NOT_FOUND,
                        null
                );
            }

            Path path = file.toPath();
            byte[] fileBytes = Files.readAllBytes(path);
            String mimeType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(fileBytes);

        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "An error occurred while downloading profile picture: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    private boolean checkFileIsImage(String filename) {
        return (filename != null && isImageFile(filename));
    }

    private boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") || lowerCaseFileName.endsWith(".png");
    }

    @Scheduled(cron = "0 0 0 * * *")  // This runs every midnight
    public void updateEmployeeAges() {
        List<Employee> employees = employeeRepo.findAll();
        for (Employee employee : employees) {
            long ageInDays = calculateAgeInDays(employee.getBirthday());
            employee.setCurrentAgeInDays(ageInDays);
        }
        employeeRepo.saveAll(employees);
    }

    private long calculateAgeInDays(Date birthday) {
        LocalDate birthDate = convertToLocalDate(birthday);
        return ChronoUnit.DAYS.between(birthDate, LocalDate.now());
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
