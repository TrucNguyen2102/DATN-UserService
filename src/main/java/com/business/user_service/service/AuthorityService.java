package com.business.user_service.service;

import com.business.user_service.entity.Authority;

public interface AuthorityService {

    Authority findByName(String name);
}
