package com.business.user_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Role_User> users;

//    @ManyToMany
//    @JoinTable(
//            name = "role_permission", // Tên bảng trung gian
//            joinColumns = @JoinColumn(name = "role_id"), // Khóa ngoại từ Role
//            inverseJoinColumns = @JoinColumn(name = "permission_id") // Khóa ngoại từ Permission
//    )
////    private Set<Permission> permissions;
//    private Set<Permission> permissions = new HashSet<>();

    public Role() {

    }

//    public Role(Integer id, String name) {
//        this.id = id;
//        this.name = name;
//    }



    public Role(Integer id, String name, Set<Role_User> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public Role(Integer id, String name) {
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

    public Set<Role_User> getUsers() {
        return users;
    }

    public void setUsers(Set<Role_User> users) {
        this.users = users;
    }

//    public Set<Permission> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<Permission> permissions) {
//        this.permissions = permissions;
//    }
}
