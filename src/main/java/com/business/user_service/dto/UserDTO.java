package com.business.user_service.dto;

import com.business.user_service.entity.User;
import com.business.user_service.entity.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Integer id;
    private String fullName;

    private LocalDate birthDay;
    private String phone;

    private String email;
    private UserStatus status;
    private Set<String> roles;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO(Integer id, String fullName, LocalDate birthDay, String phone, String email, UserStatus status, Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public UserDTO() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    // Phương thức từ User đến UserDTO
    public static UserDTO fromUser(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(roleUser -> roleUser.getRole().getName()) // Lấy tên từ đối tượng Role
                .collect(Collectors.toSet());

        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getBirthDay(),
                user.getPhone(),
                user.getEmail(),
                user.getStatus(),
                roleNames,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
