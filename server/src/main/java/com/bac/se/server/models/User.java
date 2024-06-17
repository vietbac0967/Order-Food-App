package com.bac.se.server.models;

import com.bac.se.server.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@NoArgsConstructor
@Table(name = "t_user")
@AllArgsConstructor
@Builder
@Data
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 10,nullable = false,unique = true)
    private String phone;
    @Column(length = 100,nullable = false,unique = true)
    private String email;
    private String password;
    private String address;
    private Date createdAt;
    private Role role;
}
