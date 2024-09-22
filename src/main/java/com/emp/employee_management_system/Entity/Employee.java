package com.emp.employee_management_system.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints=@UniqueConstraint(columnNames = "email"))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique=true , nullable=false)
    private String email;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthday;

    private String profilePicturePath;

    private Long currentAgeInDays;

}
