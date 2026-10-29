package com.styleshop.auth.repository;
import com.styleshop.auth.model.LoginLog; import org.springframework.data.jpa.repository.JpaRepository;
public interface LoginLogRepository extends JpaRepository<LoginLog,Long>{ java.util.List<LoginLog> findTop500ByOrderByLoginTimeDesc(); }
