package com.emp.employee_management_system.Repository;

import com.emp.employee_management_system.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee,Long> {
}
