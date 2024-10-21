package com.business.user_service.entity;

import com.business.user_service.dto.PermissionDTO;
import com.business.user_service.dto.RoleDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "permissions") // Chỉ ra rằng trường 'permissions' bên Role quản lý mối quan hệ này
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//
////    private Set<Role_Permission> roles =  new HashSet<>();
//
//    private Set<Role> roles;

    public Permission() {

    }

    public Permission(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Phương thức chuyển đổi thành PermissionDTO
    public PermissionDTO toDto() {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        // Chuyển đổi danh sách vai trò nếu cần
        List<RoleDTO> roleDTOs = this.roles.stream()
                .map(role -> {
                    RoleDTO roleDto = new RoleDTO();
                    roleDto.setId(role.getId());
                    roleDto.setName(role.getName());
                    return roleDto;
                }).collect(Collectors.toList());
        dto.setRoles(roleDTOs);
        return dto;
    }
}
