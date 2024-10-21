package com.business.user_service.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // Tạo một đối tượng SecureRandom để đảm bảo ngẫu nhiên và an toàn
        SecureRandom secureRandom = new SecureRandom();

        // Tạo một mảng byte có kích thước 32 bytes (256-bit key)
        byte[] key = new byte[32];

        // Sinh ngẫu nhiên các byte
        secureRandom.nextBytes(key);

        // Mã hóa thành chuỗi Base64 để sử dụng
        String secretKey = Base64.getEncoder().encodeToString(key);

        // In ra khóa bí mật
        System.out.println("Khóa bí mật của bạn: " + secretKey);
    }
}
