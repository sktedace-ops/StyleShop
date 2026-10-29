package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.LoginLogMapper;
import com.example.vscodeshop.model.LoginLog;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class LoginLogRepositoryImpl implements LoginLogRepository {
    private final LoginLogMapper mapper;
    public LoginLogRepositoryImpl(LoginLogMapper mapper) { this.mapper = mapper; }
    public int insert(LoginLog log) { return mapper.insert(log); }
    public List<LoginLog> findAll() { return mapper.findAll(); }
}
