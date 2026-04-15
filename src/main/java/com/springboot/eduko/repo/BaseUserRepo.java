package com.springboot.eduko.repo;

import com.springboot.eduko.model.BaseUser;
import com.springboot.eduko.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseUserRepo extends JpaRepository<BaseUser,Long> {
    @Query("SELECT u FROM BaseUser u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<BaseUser> findByEmailWithRoles(@Param("email") String email);

    BaseUser findBaseUsersByEmail(String email);

}
