package com.emp.employee_management_system.Repository;

import com.emp.employee_management_system.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Integer> {
}
