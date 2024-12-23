package com.business.user_service.schedule;

import com.business.user_service.entity.User;
import com.business.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserUnlockScheduler {

    @Autowired
    private UserService userService;

    public UserUnlockScheduler(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 * * * * *")  // Chạy mỗi phút
    public void autoUnlockAccounts() {
        List<User> blockedUsers = userService.getBlockedUsers(); // Lấy danh sách tài khoản bị khóa
        for (User user : blockedUsers) {
            userService.unlockUserAccountIfExpired(user.getId());
        }
        //System.out.println("Quá trình kiểm tra và mở khóa tài khoản hoàn tất.");
    }
}
