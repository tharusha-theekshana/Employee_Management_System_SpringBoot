package com.emp.employee_management_system.Repository;

import com.emp.employee_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {

}
