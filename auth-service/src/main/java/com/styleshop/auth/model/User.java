package com.styleshop.auth.model;
import jakarta.persistence.*;
@Entity @Table(name="users",uniqueConstraints=@UniqueConstraint(columnNames="username"))
public class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private String username; @Column(nullable=false) private String password; @Column(nullable=false,length=20) private String role="USER"; public Long getId(){return id;} public String getUsername(){return username;} public void setUsername(String v){username=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;} public String getRole(){return role;} public void setRole(String v){role=v;} }
