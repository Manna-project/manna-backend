package com.manna.auth.repository;

import com.manna.auth.entity.LoginType;
import com.manna.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginTypeAndProviderId(
            LoginType loginType,
            String providerId
    );
}
