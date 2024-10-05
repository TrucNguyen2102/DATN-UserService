package com.business.user_service.service;

import com.business.user_service.entity.Authority;
import com.business.user_service.entity.User;

public interface UserService {
    User findByPhone(String phone);

    void saveUser(User user);


    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);



}
