package com.capgroup.dcip.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.capgroup.dcip.domain.identity.Role;

/**
 * JPA Repository for CRUD operations on a Role 
 */
@Repository
public interface RoleRepository extends TemporalEntityRepository<Role>{

}
