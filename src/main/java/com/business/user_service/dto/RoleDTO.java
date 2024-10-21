package com.business.user_service.dto;

import com.business.user_service.entity.Role;

public class RoleDTO {
    private Integer id;
    private String name;

    public RoleDTO() {

    }

    public RoleDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDTO(String name) {
        this.name = name;
    }

    // Constructor mới nhận đối tượng Role
    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
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
}
