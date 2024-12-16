package com.business.user_service.dto;

import java.time.LocalDate;
import java.util.Date;

public class ManagerDTO {
    private String fullName;
//    private Date birthDay;
    private LocalDate birthDay;
    private String phone;
    private String email;

    private String password;
    private String status;
    private String role;

    public ManagerDTO() {

    }

    public ManagerDTO(String fullName, LocalDate birthDay, String phone, String email, String password, String status, String role) {
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
