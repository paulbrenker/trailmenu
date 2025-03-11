package com.nutrike.core.dto

class PageInfoDto(
    val pageSize: Int,
    val hasNext: Boolean,
    val endCursor: String?,
)
