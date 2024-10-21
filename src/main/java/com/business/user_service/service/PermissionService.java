package com.business.user_service.service;

import com.business.user_service.dto.PermissionDTO;
import com.business.user_service.entity.Permission;

import java.util.List;

public interface PermissionService {
//    List<Permission> getAllPermissions();

    List<Permission> getAllPermissions();
//    Permission addPermission(PermissionDTO permissionDTO);

    Permission save(Permission permission);

    PermissionDTO updatePermission(Integer id, PermissionDTO permissionDTO);

    void deletePermission(Integer id);
//
//    Permission updatePermission(Integer id, Permission permission);
//
//    void deletePermission(Integer id);


}
