package com.emp.employee_management_system.Configurations;

import com.emp.employee_management_system.Entity.Role;
import com.emp.employee_management_system.Repository.RoleRepo;
import com.emp.employee_management_system.Utils.Enum.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.findByName(RoleName.USER).isEmpty()) {
            roleRepo.save(new Role(RoleName.USER));
        }
        if (roleRepo.findByName(RoleName.ADMIN).isEmpty()) {
            roleRepo.save(new Role(RoleName.ADMIN));
        }
    }
}
