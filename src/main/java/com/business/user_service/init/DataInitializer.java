package com.business.user_service.init;

import com.business.user_service.entity.User;
import com.business.user_service.repository.AuthorityRepo;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityRepo authorityRepo;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra main admin đã tồn tại trong hệ thống hay chưa
        if (userService.findByPhone("0703127885") == null) {
            // Nếu chưa, tạo mới tài khoản main admin
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            User admin = new User();
            admin.setFullName("ADMIN");
            admin.setPhone("0703127885");
            admin.setEmail("nguyenthanhtruc02012002@gmail.com");
            admin.setPassword(encoder.encode("@Admin123")); // Mã hóa mật khẩu
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setAuthority(authorityRepo.findByName("ADMIN")); // Gán quyền admin

            // Lưu admin vào database
            userService.saveUser(admin);
            System.out.println("Admin account has been created.");
        } else {
            System.out.println("Admin account already exists.");
        }
    }

}
