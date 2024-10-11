package com.business.user_service.controller;

import com.business.user_service.dto.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.List;

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

            // Cập nhật trạng thái người dùng về "Đang hoạt động"
            user.setStatus("Đang hoạt động");
            userService.updateUser(user); // Lưu trạng thái mới vào cơ sở dữ liệu

            // Lấy quyền từ Authority
            String authority = user.getAuthority().getName();

            // Tạo phản hồi
            AuthenticationResponse response = new AuthenticationResponse();
            response.setId(user.getId());
            response.setPhone(user.getPhone());
            response.setAuthority(authority);
            response.setFullName(user.getFullName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thông tin đăng nhập không chính xác.");
        }
    }


    @PutMapping("/{id}/logout")
    public ResponseEntity<Void> logout(@PathVariable Integer id) {
        User user = userService.findById(id); // Tìm người dùng theo ID
        if (user != null) {
            user.setStatus("Đã đăng xuất"); // Cập nhật trạng thái
            userService.updateUser(user); // Lưu thay đổi
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // API thêm nhân viên
    @PostMapping("/staffs/add")
    public ResponseEntity<String> addUser(@RequestBody StaffDTO staffDTO) {
        if (staffDTO.getAuthority() == null) {
            return ResponseEntity.badRequest().body("Quyền không hợp lệ.");
        }
        try {
            userService.addStaff(staffDTO);
            return ResponseEntity.ok("Nhân viên đã được thêm thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi thêm nhân viên.");
        }
    }


    // API khóa nhân viên
    @PutMapping("/staffs/lock/{id}")
    public ResponseEntity<String> lockUser(@PathVariable Integer id) {
        try {
            userService.lockUser(id);
            return ResponseEntity.ok("Nhân viên đã bị khóa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi khóa nhân viên.");
        }
    }

    // API mở khóa nhân viên
    @PostMapping("/staffs/unlock")
    public ResponseEntity<String> unlockUser(@RequestBody UnlockStaffDTO unlockStaffDTO) {
        try {
            userService.unlockUser(unlockStaffDTO.getUserId());
            return ResponseEntity.ok("Nhân viên đã được mở khóa và đang hoạt động.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')") // Chỉ cho phép admin thực hiện
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/fullName")
    public ResponseEntity<User> getUser(@RequestParam String fullName) {
        User user = userService.findByFullName(fullName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

}
