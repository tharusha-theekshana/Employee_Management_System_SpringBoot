package com.emp.employee_management_system.Services;

import com.emp.employee_management_system.Dto.UserDto;
import com.emp.employee_management_system.Dto.UserLoginDto;
import com.emp.employee_management_system.Dto.UserRegisterDto;
import com.emp.employee_management_system.Entity.Role;
import com.emp.employee_management_system.Entity.User;
import com.emp.employee_management_system.Jwt.JwtService;
import com.emp.employee_management_system.Repository.RoleRepo;
import com.emp.employee_management_system.Repository.UserRepo;
import com.emp.employee_management_system.Services.Impl.UserServiceImpl;
import com.emp.employee_management_system.Utils.Response.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserServiceImpl {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public ResponseEntity<Object> registerUser(UserRegisterDto userRegisterDto) {
        try {
            if (userRegisterDto.getUsername().isEmpty() || userRegisterDto.getPassword().isEmpty() || userRegisterDto.getEmail().isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Username , Email & Password cannot be empty",
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            if (userRegisterDto.getRoles().isEmpty()) {
                return ResponseHandler.generateResponse(
                        "Username , Email & Password cannot be empty",
                        HttpStatus.BAD_REQUEST,
                        null
                );
            }

            User userExist = userRepo.findByEmail(userRegisterDto.getEmail());
            if (userExist != null) {
                return ResponseHandler.generateResponse(
                        "Email already exists !",
                        HttpStatus.CONFLICT,
                        null
                );
            }

            User user = new User();
            user.setUsername(userRegisterDto.getUsername());
            user.setEmail(userRegisterDto.getEmail());
            user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

            Set<Role> roles = new HashSet<>();
            if (userRegisterDto.getRoles() != null) {
                for (Role role : userRegisterDto.getRoles()) {
                    Role existingRole = roleRepo.findById(role.getId()).orElseThrow(() ->
                            new RuntimeException("Role not found: " + role.getId()));
                    roles.add(existingRole);
                }
                user.setRoles(roles);
            }


            userRepo.save(user);
            return ResponseHandler.generateResponse(
                    "User Registered Successfully!",
                    HttpStatus.OK,
                    null
            );

        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "Something went wrong : " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    @Override
    public ResponseEntity<Object> login(UserLoginDto userLoginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword())
            );

            var user = userRepo.findByUsername(userLoginDto.getUsername());

            UserDto userDto = getUserWithRoles(user.getId());

            var jwtToken = jwtService.generateToken(userDto);

            return ResponseHandler.generateResponse(
                    "User login successfully!",
                    HttpStatus.OK,
                    jwtToken
            );
        } catch (BadCredentialsException e) {
                return ResponseHandler.generateResponse(
                        "Invalid username or password!",
                        HttpStatus.UNAUTHORIZED,
                        null
                );

        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "Something went wrong : " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null
            );
        }
    }

    public UserDto getUserWithRoles(Long userId) {
        List<Object[]> results = userRepo.findUserWithRoles(userId);
        if (results.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String username = null;
        String email = null;
        Set<String> roleSet = new HashSet<>();

        for (Object[] row : results) {
            if (username == null) {
                username = (String) row[1];
            }
            if (email == null) {
                email = (String) row[2];
            }
            roleSet.add((String) row[3]);
        }

        List<String> roles = new ArrayList<>(roleSet);
        return new UserDto(userId, username, email, roles);
    }
}
