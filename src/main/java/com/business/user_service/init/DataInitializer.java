package com.business.user_service.init;

import com.business.user_service.entity.*;
import com.business.user_service.repository.AuthorityRepo;
import com.business.user_service.repository.RoleRepo;
import com.business.user_service.repository.RoleUserRepo;
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
    private RoleRepo roleRepo;

    @Autowired
    private RoleUserRepo roleUserRepo;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra main admin đã tồn tại trong hệ thống hay chưa
        if (userService.findByPhone("0936596049") == null) {
            // Nếu chưa, tạo mới tài khoản main admin
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            User admin = new User();
            admin.setFullName("ADMIN");
            admin.setPhone("0936596049");
            admin.setEmail("nguyenthanhtruc010202@gmail.com");
            admin.setPassword(encoder.encode("@Admin123")); // Mã hóa mật khẩu
//            admin.setStatus("Đang hoạt động"); // Gán trạng thái mặc định
            admin.setStatus(UserStatus.ACTIVE);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
//            admin.setAuthority(authorityRepo.findByName("ADMIN")); // Gán quyền admin


            // Lưu admin vào database
            userService.saveUser(admin);
            System.out.println("Admin account has been created.");

            // Tạo Role cho admin (có thể là ROLE_ADMIN)
            Role role = roleRepo.findByName("ADMIN");
            if (role != null) {
                // Tạo Role_User và lưu vào database
                Role_User roleUser = new Role_User();
                RoleUserId roleUserId = new RoleUserId(role.getId(), admin.getId()); // Sử dụng ID của role và user
                roleUser.setId(roleUserId);
                roleUser.setUser(admin);
                roleUser.setRole(role);

                // Lưu Role_User vào database
                roleUserRepo.save(roleUser);
                System.out.println("Role for admin has been assigned.");
            }
        } else {
            System.out.println("Admin account already exists.");
        }
    }

}
