package com.business.user_service.service;

import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApiCallService apiCallService;
    public Integer countTotalUsers() {
        return Math.toIntExact(userRepo.count());
    }

    public Integer countTotalApiCalls() {
        return apiCallService.getApiCallCount();
    }


}
