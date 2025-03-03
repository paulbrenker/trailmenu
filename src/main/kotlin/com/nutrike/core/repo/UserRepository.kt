package com.nutrike.core.repo

import com.nutrike.core.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    fun findUserEntityByUsernameAndPasswordAndApprovalIsTrue(
        username: String,
        password: String,
    ): UserEntity?
}
