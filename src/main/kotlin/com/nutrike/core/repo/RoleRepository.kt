package com.nutrike.core.repo

import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<RoleEntity, RoleType>
