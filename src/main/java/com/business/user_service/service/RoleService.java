package com.business.user_service.service;

import com.business.user_service.dto.RoleDTO;
import com.business.user_service.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
//    List<Role> getAllRoles();

//    void addRoleWithPermissions(String roleName, Set<String> permissionNames);

    List<Role> getAllRoles();

    Role findById(Integer id);
}
