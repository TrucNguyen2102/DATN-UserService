package com.business.user_service.controller;

import com.business.user_service.dto.AuthenticationRequest;
import com.business.user_service.dto.AuthenticationResponse;
import com.business.user_service.dto.RegisterRequest;
import com.business.user_service.entity.Authority;
import com.business.user_service.entity.User;
import com.business.user_service.service.AuthorityService;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
        try {
            // Xác thực người dùng bằng số điện thoại và mật khẩu
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin người dùng
            User user = userService.findByPhone(request.getPhone());

            // Lấy quyền từ Authority
            String authority = user.getAuthority().getName();

            // Tạo phản hồi
            AuthenticationResponse response = new AuthenticationResponse();
            response.setPhone(user.getPhone());
            response.setAuthority(authority);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
        }
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ cho phép admin thực hiện
    public ResponseEntity<?> registerOwner(@RequestBody RegisterRequest request) {
        // Kiểm tra mật khẩu
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu không khớp.");
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (userService.existsByPhone(request.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
        }

        // Kiểm tra email đã tồn tại
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
        }

        // Tạo đối tượng User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Thiết lập quyền cho owner
        Authority authority = authorityService.findByName("OWNER");
        if (authority == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quyền OWNER không tồn tại.");
        }
        user.setAuthority(authority);

        // Lưu người dùng
        userService.saveUser(user);
        return ResponseEntity.ok("Tài khoản owner đã được tạo thành công!");
    }

    @PostMapping("/customer/register")
    @PreAuthorize("hasAuthority('CUSTOMER')") // Chỉ cho phép admin thực hiện
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterRequest request) {
        // Kiểm tra mật khẩu
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu không khớp.");
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (userService.existsByPhone(request.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số điện thoại đã được sử dụng.");
        }

        // Kiểm tra email đã tồn tại
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được sử dụng.");
        }

        // Tạo đối tượng User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Thiết lập quyền cho customer
        Authority authority = authorityService.findByName("CUSTOMER");
        if (authority == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quyền CUSTOMER không tồn tại.");
        }
        user.setAuthority(authority);

        // Lưu người dùng
        userService.saveUser(user);
        return ResponseEntity.ok("Tài khoản customer đã được tạo thành công!");
    }

}
