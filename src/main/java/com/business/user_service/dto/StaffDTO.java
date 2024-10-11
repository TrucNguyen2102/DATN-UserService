package com.business.user_service.dto;

public class StaffDTO {
    private String fullName;
    private String phone;
    private String email;

    private String password;
    private String status;
    private String authority;


    public StaffDTO() {

    }

    public StaffDTO(String fullName, String email, String phone, String password, String authority, String status) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.authority = authority;
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
