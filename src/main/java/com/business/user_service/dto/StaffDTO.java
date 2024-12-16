package com.business.user_service.dto;

import java.time.LocalDate;
import java.util.Date;

public class StaffDTO {
    private String fullName;

    private LocalDate birthDay;
    private String phone;
    private String email;

    private String password;
    private String status;
    private String role;


    public StaffDTO() {

    }

    public StaffDTO(String fullName, LocalDate birthDay, String email, String phone, String password, String role, String status) {
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
