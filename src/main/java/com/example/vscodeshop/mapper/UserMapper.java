package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User findByUsername(@Param("username") String username);
    int countAll();
    int insert(User user);
    int updatePasswordAndRole(User user);
}
