package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.User;

public interface UserDao {
    User findByUsername(String username);
    int countAll();
    int insert(User user);
    int updatePasswordAndRole(User user);
}
