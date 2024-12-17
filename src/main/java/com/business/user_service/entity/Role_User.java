package com.business.user_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "role_user")
public class Role_User implements Serializable {
    @EmbeddedId
    private RoleUserId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public Role_User() {

    }

    public Role_User(Role role, User user) {
        this.role = role;
        this.user = user;
        this.id = new RoleUserId(role.getId(), user.getId());
    }

    public RoleUserId getId() {
        return id;
    }

    public void setId(RoleUserId id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
