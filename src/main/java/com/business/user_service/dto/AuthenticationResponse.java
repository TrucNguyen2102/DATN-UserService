package com.business.user_service.dto;

public class AuthenticationResponse {
    private String phone; // Số điện thoại
    private String authority; // Quyền

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
