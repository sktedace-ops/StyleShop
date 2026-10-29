package com.example.vscodeshop.mapper;

import com.example.vscodeshop.model.LoginLog;
import java.util.List;

public interface LoginLogMapper {
    int insert(LoginLog log);
    List<LoginLog> findAll();
}
