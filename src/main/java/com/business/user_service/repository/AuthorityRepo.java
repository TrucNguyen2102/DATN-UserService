package com.business.user_service.repository;

import com.business.user_service.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Integer> {
    Authority findByName(String name);
}
