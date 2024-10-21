package com.business.user_service.controller;

import com.business.user_service.dto.RoleDTO;
import com.business.user_service.entity.Role;
import com.business.user_service.repository.RoleRepo;
import com.business.user_service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepo roleRepo;

    // Lấy danh sách tất cả vai trò
//    @GetMapping("/roles/all")
//    public List<Role> getAllRoles() {
//        return roleService.getAllRoles();
//    }


//    @GetMapping("/roles/all")
//    public ResponseEntity<List<RoleDTO>> getAllRoles() {
//        List<RoleDTO> roles = roleService.getAllRoles();
//        return ResponseEntity.ok(roles);
//    }

    @GetMapping("/admins/roles/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDTO> roleDtos = roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }


//    @PostMapping("/roles/add")
//    public ResponseEntity<String> addRoleWithPermissions(
//            @RequestParam String roleName,
//            @RequestParam Set<String> permissionNames) {
//
//        roleService.addRoleWithPermissions(roleName, permissionNames);
//        return ResponseEntity.ok("Role và quyền hạn đã được thêm thành công!");
//    }


//    @GetMapping("/roles/all")
//    public List<Role> getAllRoles() {
//        return roleRepo.findAll();
//    }

}
