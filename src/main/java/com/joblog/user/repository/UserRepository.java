package com.joblog.user.repository;

import com.joblog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    // ✅ OAuth2 인증용 - trace 제외
    @Query("SELECT u FROM User u WHERE u.email = :email")
    @Transactional(readOnly = true)
    Optional<User> findByEmailForOAuth(@Param("email") String email); // AOP 비대상
}
