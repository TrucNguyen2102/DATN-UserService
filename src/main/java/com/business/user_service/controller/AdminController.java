package com.business.user_service.controller;

import com.business.user_service.dto.ManagerDTO;
import com.business.user_service.dto.StaffDTO;
import com.business.user_service.dto.UserDTO;
import com.business.user_service.entity.User;
import com.business.user_service.exception.ResourceNotFoundException;
import com.business.user_service.service.AdminService;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    //api sd
    @GetMapping("/admins/total-users")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Integer> getTotalUsers() {
        try {
            Integer totalUsers = adminService.countTotalUsers(); // Lấy tổng số người dùng từ service
            return ResponseEntity.ok(totalUsers);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/admins/active/count")
    public ResponseEntity<Integer> countActiveUsers() {
        Integer activeUserCount = adminService.countActiveUsers();
        return ResponseEntity.ok(activeUserCount);
    }

    @GetMapping("/admins/in_active/count")
    public ResponseEntity<Integer> countInActiveUsers() {
        Integer inActiveUserCount = adminService.countInActiveUsers();
        return ResponseEntity.ok(inActiveUserCount);
    }

    @GetMapping("/admins/lock/count")
    public ResponseEntity<Integer> countLockUsers() {
        Integer inLockUserCount = adminService.countLockUsers();
        return ResponseEntity.ok(inLockUserCount);
    }

    @GetMapping("/admins/guest/count")
    public ResponseEntity<Integer> countGuestUsers() {
        Integer inGuestCount = adminService.countGuests();
        return ResponseEntity.ok(inGuestCount);
    }

    //api sd

    // Phương thức để lấy tổng số API được gọi
//    @GetMapping("/admins/total-api-calls")
////    @PreAuthorize("hasAuthority('ADMIN')")
//    public Integer getTotalApiCalls() {
//        return adminService.countTotalApiCalls();
//    }

    //thêm tk quản lý
    // API thêm nhân viên
    @PostMapping("/admins/managers/add")

    public ResponseEntity<String> addUser(@RequestBody ManagerDTO managerDTO) {
        if (managerDTO.getRole() == null) {
            return ResponseEntity.badRequest().body("Quyền không hợp lệ.");
        }
        try {
            userService.addManager(managerDTO);
            return ResponseEntity.ok("Quản lý đã được thêm thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi thêm quản lý.");
        }
    }

    @PutMapping("/admins/managers/update/{id}")
    public ResponseEntity<?> updateManager(@PathVariable Integer id, @RequestBody ManagerDTO request) {
        try {
            User updatedManager = userService.updateManager(id, request);
            return ResponseEntity.ok(updatedManager);
        } catch (ResourceNotFoundException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Manager not found.");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating manager.");
        }
    }

    @PutMapping("/admins/managers/lock/{id}")
    public ResponseEntity<String> lockUser(@PathVariable Integer id) {
        try {
            userService.lockUser(id);
            return ResponseEntity.ok("Quản lý đã bị khóa.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi khóa quản lý.");
        }
    }

    @PutMapping("/admins/managers/unlock/{id}")
    public ResponseEntity<String> unlockUser(@PathVariable Integer id) {
        try {
            userService.unlockUser(id);
            return ResponseEntity.ok("Quản lý đã được mở khóa và đang hoạt động.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
