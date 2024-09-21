package com.emp.employee_management_system.Repository;

import com.emp.employee_management_system.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    User findByUsername(String username);

    Optional<User> findById(Long id);

    @Query("SELECT u.id, u.username, u.email, r.roleName FROM User u JOIN u.roles r WHERE u.id = :id")
    List<Object[]> findUserWithRoles(@Param("id") Long id);
}
