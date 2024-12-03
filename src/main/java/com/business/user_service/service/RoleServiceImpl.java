package com.business.user_service.service;

import com.business.user_service.dto.RoleDTO;
//import com.business.user_service.entity.Permission;
import com.business.user_service.entity.Role;
//import com.business.user_service.repository.PermissionRepo;
import com.business.user_service.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepo roleRepo;
//    @Autowired
//    private PermissionRepo permissionRepo;

    // Lấy tất cả vai trò
    @Override
//    public List<RoleDTO> getAllRoles() {
//        return roleRepo.findAll()
//                .stream()
//                .map(role -> new RoleDTO(role.getId(), role.getName()))
//                .collect(Collectors.toList());
//    }

//    public List<RoleDTO> getAllRoles() {
//        List<Role> roles = roleRepo.findAll();
//        return roles.stream().map(role -> {
//            RoleDTO roleDto = new RoleDTO();
//            roleDto.setId(role.getId());
//            roleDto.setName(role.getName());
//            return roleDto;
//        }).collect(Collectors.toList());
//    }

    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    public Role findById(Integer id) {
        return roleRepo.findById(id).orElse(null);
    }


    // Hàm thêm Role và Permission mới
//    public void addRoleWithPermissions(String roleName, Set<String> permissionNames) {
//        Role role = new Role();
//        role.setName(roleName);
//
//        Set<Permission> permissions = new HashSet<>();
//        for (String permissionName : permissionNames) {
//            Permission permission = permissionRepo.findByName(permissionName);
//            if (permission == null) {
//                permission = new Permission();
//                permission.setName(permissionName);
//                permissionRepo.save(permission); // Lưu permission mới nếu chưa có
//            }
//            permissions.add(permission);
//        }
//
//        role.setPermissions(permissions);
//        roleRepo.save(role); // Lưu role với các permissions
//    }


}
