package com.business.user_service.repository;

import com.business.user_service.dto.UserDTO;
import com.business.user_service.entity.User;
import com.business.user_service.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


//    List<User> findUserByFullNameOrPhone(String fullName, String phone);

    List<User> findByFullNameContaining(String fullName);
    List<User> findByPhoneContaining(String phone);
    List<User> findByFullNameContainingOrPhoneContaining(String fullName, String phone);

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles ru JOIN ru.role r WHERE r.name = :name")
    Page<User> findUsersByRoleName(@Param("name") String name, Pageable pageable);

    List<User> findByStatus(UserStatus status);

    Integer countByStatus(UserStatus status);

    List<User> findAllByStatus(UserStatus status);


}
