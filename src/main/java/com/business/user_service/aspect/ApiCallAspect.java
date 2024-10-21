package com.business.user_service.aspect;

import com.business.user_service.service.ApiCallService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiCallAspect {

    @Autowired
    private ApiCallService apiCallService;

    // Theo dõi tất cả các phương thức trong Controller
    @After("execution(* com.business.user_service.controller..*(..))")
    public void countApiCalls(JoinPoint joinPoint) {
        apiCallService.incrementApiCallCount();
    }
}
