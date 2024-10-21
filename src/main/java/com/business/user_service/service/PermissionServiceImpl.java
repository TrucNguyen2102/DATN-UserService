package com.business.user_service.service;

import com.business.user_service.dto.PermissionDTO;
import com.business.user_service.dto.RoleDTO;
import com.business.user_service.entity.Permission;
import com.business.user_service.entity.Role;
import com.business.user_service.entity.RolePermissionId;
import com.business.user_service.entity.Role_Permission;
import com.business.user_service.repository.PermissionRepo;
import com.business.user_service.repository.RolePermissionRepo;
import com.business.user_service.repository.RoleRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService{

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PermissionRepo permissionRepo;
    @Autowired
    private RolePermissionRepo rolePermissionRepo;
//
//    // Lấy tất cả quyền hạn
//    public List<Permission> getAllPermissions() {
//        return permissionRepo.findAll();
//    }
//
//    @Override
//    public Permission addPermission(Permission permission) {
//        return permissionRepo.save(permission);
//    }
//
//    @Override
//    public Permission updatePermission(Integer id, Permission permission) {
//        try {
//            if (permissionRepo.existsById(id)) {
//                permission.setId(id);
//                return permissionRepo.save(permission);
//            } else {
//                throw new RuntimeException("Permission not found with ID: " + id);
//            }
//        } catch (Exception e) {
//            // Xử lý lỗi và có thể ghi lại thông tin log nếu cần
//            throw new RuntimeException("Error updating permission: " + e.getMessage());
//        }
//    }
//
//
//    @Override
//    public void deletePermission(Integer id) {
//        try {
//            if (permissionRepo.existsById(id)) {
//                permissionRepo.deleteById(id);
//            } else {
//                throw new RuntimeException("Permission not found with ID: " + id);
//            }
//        } catch (Exception e) {
//            // Xử lý lỗi và có thể ghi lại thông tin log nếu cần
//            throw new RuntimeException("Error deleting permission: " + e.getMessage());
//        }
//    }

    public List<Permission> getAllPermissions() {
        return permissionRepo.findAll();
    }

    public Permission save(Permission permission) {
        return permissionRepo.save(permission);
    }


//    public Permission addPermission(PermissionDTO permissionDTO) {
//        // Tạo quyền hạn mới
//        Permission newPermission = new Permission();
//        newPermission.setName(permissionDTO.getName());
//
//        // Tìm các vai trò dựa trên tên (nếu có)
//        List<Role> roles = permissionDTO.getRoles().stream()
//                .map(roleName -> roleRepo.findByName(String.valueOf(roleName)))
//                .filter(Objects::nonNull) // Lọc ra những vai trò không null
//                .collect(Collectors.toList());
//
//        // Liên kết các vai trò với quyền hạn mới
//        newPermission.setRoles(new HashSet<>(roles));
//
//        // Lưu quyền hạn vào cơ sở dữ liệu
//        return permissionRepo.save(newPermission);
//    }


    // Phương thức cập nhật quyền hạn
    @Transactional
    public PermissionDTO updatePermission(Integer permissionId, PermissionDTO permissionDTO) {
        // Tìm Permission theo ID
        Permission permission = permissionRepo.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));

        // Cập nhật tên Permission
        permission.setName(permissionDTO.getName());

        // Lấy danh sách vai trò từ DTO và thiết lập
        Set<Role> roles = new HashSet<>();
        for (RoleDTO roleDto : permissionDTO.getRoles()) {
            Role role = roleRepo.findById(roleDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            roles.add(role);
        }
        permission.setRoles(roles);

        // Lưu Permission
        Permission updatedPermission = permissionRepo.save(permission);

        // Xóa tất cả các RolePermission cũ
        rolePermissionRepo.deleteByPermissionId(permissionId);

        // Lưu thông tin vào bảng role_permission
        for (Role role : roles) {
            Role_Permission rolePermission = new Role_Permission();
            rolePermission.setRole(role);
            rolePermission.setPermission(updatedPermission); // Thiết lập quyền vừa lưu

            // Tạo và thiết lập ID cho role_permission
            RolePermissionId rolePermissionId = new RolePermissionId(role.getId(), updatedPermission.getId());
            rolePermission.setId(rolePermissionId); // Thiết lập ID trước khi lưu

            rolePermissionRepo.save(rolePermission); // Lưu vào bảng role_permission
        }

        // Trả về DTO sau khi cập nhật
        return new PermissionDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Integer id) {
        permissionRepo.deleteById(id);
    }


}
