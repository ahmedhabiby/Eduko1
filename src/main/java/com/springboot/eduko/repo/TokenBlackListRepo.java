package com.springboot.eduko.repo;

import com.springboot.eduko.model.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TokenBlackListRepo extends JpaRepository<TokenBlackList,Long> {
    boolean existsByToken(String token);

    void deleteByExpireDateBefore(Date date);
}
