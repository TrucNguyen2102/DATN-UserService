package com.business.user_service.service;

import com.business.user_service.entity.UserStatus;
import com.business.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{
    @Autowired
    private UserRepo userRepo;

//    @Autowired
//    private ApiCallService apiCallService;
    public Integer countTotalUsers() {
        return Math.toIntExact(userRepo.count());
    }

    public Integer countActiveUsers() {
        return (userRepo.countByStatus(UserStatus.ACTIVE));
    }

    public Integer countInActiveUsers() {
        return (userRepo.countByStatus(UserStatus.INACTIVE));
    }

    public Integer countLockUsers() {
        return (userRepo.countByStatus(UserStatus.BLOCKED));
    }

    public Integer countGuests() {
        return (userRepo.countByStatus(UserStatus.GUEST));
    }

//    public Integer countTotalApiCalls() {
//        return apiCallService.getApiCallCount();
//    }


}
