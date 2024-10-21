package com.business.user_service.repository;

import com.business.user_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {
    Role findByName(String admin);

    List<Role> findAll();
}
