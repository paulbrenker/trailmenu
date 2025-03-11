package com.nutrike.core.dto

data class PageDto<T>(
    val pageInfo: PageInfoDto,
    val totalCount: Int,
    val data: List<T>,
)
