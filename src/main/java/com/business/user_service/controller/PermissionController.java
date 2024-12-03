//package com.business.user_service.controller;
//
//import com.business.user_service.dto.PermissionDTO;
//import com.business.user_service.dto.RoleDTO;
//import com.business.user_service.entity.Permission;
//import com.business.user_service.entity.Role;
//import com.business.user_service.entity.RolePermissionId;
//import com.business.user_service.entity.Role_Permission;
//import com.business.user_service.repository.PermissionRepo;
//import com.business.user_service.repository.RolePermissionRepo;
//import com.business.user_service.service.PermissionService;
//import com.business.user_service.service.RoleService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/users")
//public class PermissionController {
//
//    @Autowired
//    private PermissionRepo permissionRepo;
//
//    @Autowired
//    private PermissionService permissionService;
//
//    @Autowired
//    private RoleService roleService;
//    @Autowired
//    private RolePermissionRepo rolePermissionRepo;
//
//
//    // API để thêm quyền hạn mới
//
////    @PostMapping("/permissions/add")
////    public ResponseEntity<?> addPermission(@RequestBody PermissionDTO permissionDTO) {
////        try {
////            // Gọi service để lưu quyền hạn mới
////            Permission newPermission = permissionService.addPermission(permissionDTO);
////            return ResponseEntity.status(201).body(newPermission); // Trả về quyền hạn mới được tạo
////        } catch (Exception e) {
////            e.printStackTrace();
////            return ResponseEntity.status(500).body("Lỗi khi thêm quyền hạn: " + e.getMessage());
////        }
////    }
//
////    @PostMapping("/permissions/add")
////    public ResponseEntity<PermissionDTO> addPermission(@RequestBody PermissionDTO permissionDTO) {
////        try {
////            Permission permission = new Permission();
////            permission.setName(permissionDTO.getName());
////
////            // Lấy danh sách vai trò từ DTO và thiết lập
////            Set<Role> roles = new HashSet<>();
////            for (RoleDTO roleDto : permissionDTO.getRoles()) {
////                Role role = roleService.findById(roleDto.getId());
////                if (role != null) {
////                    roles.add(role);
////                }
////            }
////            permission.setRoles(roles);
////
////            Permission savedPermission = permissionService.save(permission);
////            return ResponseEntity.ok(savedPermission.toDto());
////        } catch (Exception e) {
////            e.printStackTrace();
////            return ResponseEntity.badRequest().build();
////
////        }
////    }
//
//    //api sd
//    @PostMapping("/admins/permissions/add")
//    public ResponseEntity<PermissionDTO> addPermission(@RequestBody PermissionDTO permissionDTO) {
//        try {
//            // Kiểm tra xem tên quyền đã tồn tại chưa
//            if (permissionRepo.findByName(permissionDTO.getName()).isPresent()) {
//                return ResponseEntity.badRequest().body(null); // Hoặc trả về một thông báo lỗi
//            }
//
//            Permission permission = new Permission();
//            permission.setName(permissionDTO.getName());
//
//            // Lấy danh sách vai trò từ DTO và thiết lập
//            Set<Role> roles = new HashSet<>();
//            for (RoleDTO roleDto : permissionDTO.getRoles()) {
//                Role role = roleService.findById(roleDto.getId());
//                if (role != null) {
//                    roles.add(role);
//                }
//            }
//            permission.setRoles(roles);
//
//            // Lưu Permission
//            Permission savedPermission = permissionService.save(permission);
//
//            // Lưu thông tin vào bảng role_permission
//            for (Role role : roles) {
//                Role_Permission rolePermission = new Role_Permission();
//                rolePermission.setRole(role);
//                rolePermission.setPermission(savedPermission); // Thiết lập quyền vừa lưu
//
//                // Tạo và thiết lập ID cho role_permission
//                RolePermissionId rolePermissionId = new RolePermissionId(role.getId(), savedPermission.getId());
//                rolePermission.setId(rolePermissionId); // Thiết lập ID trước khi lưu
//
//                rolePermissionRepo.save(rolePermission); // Lưu vào bảng role_permission
//            }
//
//            return ResponseEntity.ok(savedPermission.toDto());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//
//    //api sd
//
//
//    @GetMapping("/admins/permissions/all")
//    @PreAuthorize("hasAuthority('READ_PERMISSION')")
//    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
//        List<Permission> permissions = permissionService.getAllPermissions();
//        List<PermissionDTO> permissionDtos = permissions.stream().map(permission -> {
//            PermissionDTO dto = new PermissionDTO();
//            dto.setId(permission.getId());
//            dto.setName(permission.getName());
//            dto.setRoles(permission.getRoles().stream()
//                    .map(role -> {
//                        RoleDTO roleDto = new RoleDTO();
//                        roleDto.setId(role.getId());
//                        roleDto.setName(role.getName());
//                        return roleDto;
//                    }).collect(Collectors.toList()));
//            return dto;
//        }).collect(Collectors.toList());
//        return ResponseEntity.ok(permissionDtos);
//    }
//
//    //api sd
//    // Cập nhật quyền hạn
//    @PutMapping("/admins/permissions/update/{id}")
//    public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Integer id, @RequestBody PermissionDTO permissionDTO) {
//        try {
//            PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
//            return ResponseEntity.ok(updatedPermission);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 nếu không tìm thấy
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 cho lỗi khác
//        }
//    }
//
//    @DeleteMapping("/admins/permissions/delete/{id}")
//    public ResponseEntity<?> deletePermission(@PathVariable Integer id) {
//        try {
//            permissionService.deletePermission(id);
//            return ResponseEntity.ok("Quyền hạn đã được xóa thành công!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa quyền hạn: " + e.getMessage());
//        }
//    }
//
//    //api sd
//    @GetMapping("/admins/permissions/check-name/{name}")
//    public ResponseEntity<Boolean> checkPermissionNameExists(@PathVariable String name) {
//        // Chuyển tên quyền về chữ thường để so sánh
//        String lowerCaseName = name.toLowerCase();
//        boolean exists = permissionRepo.findByName(lowerCaseName).isPresent();
//        return ResponseEntity.ok(exists);
//    }
//
//
//}
