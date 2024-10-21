package com.business.user_service.repository;

import com.business.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
//    Optional<User> findByPhone(String phone); // Tìm người dùng theo số điện thoại
    User findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    User findByFullName(String fullName);


    // Phương thức sử dụng JPQL để tìm kiếm User theo tên Role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.role.name = :roleName")
    List<User> findByRoleName(String roleName);


}
