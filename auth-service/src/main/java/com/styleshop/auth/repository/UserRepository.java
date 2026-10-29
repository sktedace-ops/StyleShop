package com.styleshop.auth.repository;
import com.styleshop.auth.model.User; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByUsername(String username); }
