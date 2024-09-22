package com.emp.employee_management_system.Configurations;

import com.emp.employee_management_system.Entity.Role;
import com.emp.employee_management_system.Repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    final static String user = "USER";
    final static String admin = "ADMIN";

    //Initialize roles [ USER & ADMIN ] when project start
    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.findByRoleName(user).isEmpty()) {
            roleRepo.save(new Role(user));
        }
        if (roleRepo.findByRoleName(admin).isEmpty()) {
            roleRepo.save(new Role(admin));
        }
    }
}
