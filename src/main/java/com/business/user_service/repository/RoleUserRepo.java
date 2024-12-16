package com.business.user_service.repository;

import com.business.user_service.entity.RoleUserId;
import com.business.user_service.entity.Role_User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleUserRepo extends JpaRepository<Role_User, RoleUserId> {
    Optional<Role_User> findByUserId(Integer id);

    void deleteAllByUserId(Integer id);

    Set<Role_User> findAllByUserId(Integer id);
}
