//package com.business.user_service.dto;
//
//import com.business.user_service.entity.Permission;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class PermissionDTO {
//    private Integer id;
//    private String name; // Tên quyền hạn
//    private List<RoleDTO> roles;
//
//    public PermissionDTO() {
//
//    }
//
//    // Constructor nhận vào đối tượng Permission
//    public PermissionDTO(Permission permission) {
//        this.id = permission.getId();
//        this.name = permission.getName();
//        this.roles = permission.getRoles().stream()
//                .map(role -> new RoleDTO(role))  // Chuyển Role thành RoleDTO
//                .collect(Collectors.toList());   // Đảm bảo sử dụng List<RoleDTO>
//    }
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<RoleDTO> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(List<RoleDTO> roles) {
//        this.roles = roles;
//    }
//}
