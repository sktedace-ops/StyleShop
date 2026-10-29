package com.example.vscodeshop.repository;

import com.example.vscodeshop.mapper.UserMapper;
import com.example.vscodeshop.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserMapper mapper;
    public UserRepositoryImpl(UserMapper mapper) { this.mapper = mapper; }
    public User findByUsername(String username) { return mapper.findByUsername(username); }
    public int countAll() { return mapper.countAll(); }
    public int insert(User user) { return mapper.insert(user); }
    public int updatePasswordAndRole(User user) { return mapper.updatePasswordAndRole(user); }
}
