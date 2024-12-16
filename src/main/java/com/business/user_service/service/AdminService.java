package com.business.user_service.service;

public interface AdminService {
    Integer countTotalUsers();

    Integer countActiveUsers();

    Integer countInActiveUsers();

    Integer countLockUsers();

    Integer countGuests();

//    Integer countTotalApiCalls();
}
