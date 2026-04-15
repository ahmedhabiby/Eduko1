package com.springboot.eduko.repo;

import com.springboot.eduko.model.EduRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepo extends JpaRepository<EduRoles, Long> {

    @Query("SELECT r FROM EduRoles r WHERE r.id =:id ")
    EduRoles findByRoleId(@Param("id") Long id);
}
