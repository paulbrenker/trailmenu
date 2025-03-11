package com.nutrike.core.repo

import com.nutrike.core.entity.RoleEntity
import com.nutrike.core.entity.RoleType
import com.nutrike.core.util.CursorUtil
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class UserSpecification<T>(
    private val after: String?,
    private val roleType: RoleType?,
) : Specification<T> {
    companion object {
        private const val PAGE_FIELD = "username"
        private const val ROLES_FIELD = "roles"
    }

    @Override
    override fun toPredicate(
        root: Root<T>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder,
    ): Predicate {
        val predicates = mutableListOf<Predicate>()

        after?.let {
            predicates.add(applyPaginationFilter(root, criteriaBuilder, it))
        }
        roleType?.let {
            val rolesJoin = root.join<Set<RoleEntity>, RoleEntity>(ROLES_FIELD)
            predicates.add(criteriaBuilder.equal(rolesJoin.get<RoleType>("type"), it))
        }

        query!!.orderBy(criteriaBuilder.asc(root.get<String>(PAGE_FIELD)))
        return criteriaBuilder.and(*predicates.toTypedArray())
    }

    private fun applyPaginationFilter(
        root: Root<T>,
        criteriaBuilder: CriteriaBuilder,
        after: String,
    ): Predicate {
        val searchValue = CursorUtil.getDecodedCursor(after).getValue(PAGE_FIELD).toString()

        return criteriaBuilder.greaterThan(root.get(PAGE_FIELD), searchValue)
    }
}
