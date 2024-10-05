package com.business.user_service.service;

import com.business.user_service.entity.Authority;
import com.business.user_service.repository.AuthorityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService{
    @Autowired
    private AuthorityRepo authorityRepo;

    @Override
    public Authority findByName(String name) {
        return authorityRepo.findByName(name);
    }
}
