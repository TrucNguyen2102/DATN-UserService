package com.business.user_service.repository;

import com.business.user_service.entity.RoleUserId;
import com.business.user_service.entity.Role_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUserRepo extends JpaRepository<Role_User, RoleUserId> {
}
