package com.nutrike.core.repo

import com.nutrike.core.util.CursorUtil
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class IngredientSpecification<T>(
    private val searchTerm: String?,
    private val after: String?,
) : Specification<T> {
    companion object {
        private const val PAGE_FIELD = "name"
    }

    @Override
    override fun toPredicate(
        root: Root<T>,
        query: CriteriaQuery<*>?,
        criteriaBuilder: CriteriaBuilder,
    ): Predicate {
        val predicates = mutableListOf<Predicate>()
        searchTerm?.let {
            predicates.add(applySearchFilter(root, criteriaBuilder, it))
        }
        after?.let {
            predicates.add(applyPaginationFilter(root, criteriaBuilder, after))
        }
        query!!.orderBy(criteriaBuilder.asc(root.get<String>(PAGE_FIELD)))
        return criteriaBuilder.and(*predicates.toTypedArray())
    }

    // TODO put this function into a util class better abstract every Spec Class

    private fun applySearchFilter(
        root: Root<T>,
        criteriaBuilder: CriteriaBuilder,
        searchTerm: String,
    ): Predicate =
        criteriaBuilder.like(
            root.get(PAGE_FIELD),
            "%$searchTerm%",
        )

    private fun applyPaginationFilter(
        root: Root<T>,
        criteriaBuilder: CriteriaBuilder,
        after: String,
    ): Predicate {
        val searchValue = CursorUtil.getDecodedCursor(after).getValue(PAGE_FIELD).toString()

        return criteriaBuilder.greaterThan(root.get(PAGE_FIELD), searchValue)
    }
}
