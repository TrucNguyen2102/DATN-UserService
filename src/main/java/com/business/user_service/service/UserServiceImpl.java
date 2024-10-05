package com.business.user_service.service;

import com.business.user_service.entity.Authority;
import com.business.user_service.entity.User;
import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    // Phương thức tìm kiếm người dùng theo số điện thoại
    public User findByPhone(String phone) {
        return userRepo.findByPhone(phone);
//                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với số điện thoại: " + phone));
    }

    //lưu user
    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }


    @Override
    public boolean existsByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }




}
