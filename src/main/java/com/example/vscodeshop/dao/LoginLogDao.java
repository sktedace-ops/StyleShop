package com.example.vscodeshop.dao;

import com.example.vscodeshop.model.LoginLog;
import java.util.List;

public interface LoginLogDao {
    int insert(LoginLog log);
    List<LoginLog> findAll();
}
