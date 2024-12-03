//package com.business.user_service.entity;
//
//import jakarta.persistence.*;
//
//
//
//@Entity
//@Table(name = "role_permission")
//public class Role_Permission {
//    @EmbeddedId
//    private RolePermissionId id;
//
//    @ManyToOne
//    @MapsId("roleId")
//    @JoinColumn(name = "role_id")
//    private Role role;
//
//    @ManyToOne
//    @MapsId("permissionId")
//    @JoinColumn(name = "permission_id")
//    private Permission permission;
//
//    public Role_Permission() {
//
//    }
//
//    public Role_Permission(Role role, Permission permission) {
//        this.role = role;
//        this.permission = permission;
//        this.id = new RolePermissionId(role.getId(), permission.getId());
//    }
//
//    public Role_Permission(Integer id, Integer roleId) {
//    }
//
//    public RolePermissionId getId() {
//        return id;
//    }
//
//    public void setId(RolePermissionId id) {
//        this.id = id;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public Permission getPermission() {
//        return permission;
//    }
//
//    public void setPermission(Permission permission) {
//        this.permission = permission;
//    }
//}
