package com.business.user_service.repository;

import com.business.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
//    Optional<User> findByPhone(String phone); // Tìm người dùng theo số điện thoại
    User findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
