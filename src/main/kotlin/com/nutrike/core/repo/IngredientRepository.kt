package com.nutrike.core.repo

import com.nutrike.core.entity.IngredientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IngredientRepository :
    JpaRepository<IngredientEntity, UUID>,
    JpaSpecificationExecutor<IngredientEntity>
