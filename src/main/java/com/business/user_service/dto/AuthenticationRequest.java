package com.business.user_service.dto;

public class AuthenticationRequest {
    private String phone; // Số điện thoại
    private String password; // Mật khẩu

    // Getters và Setters
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
}
