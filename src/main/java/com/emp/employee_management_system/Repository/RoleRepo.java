package com.emp.employee_management_system.Repository;

import com.emp.employee_management_system.Entity.Role;
import com.emp.employee_management_system.Utils.Enum.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(RoleName name);
}
