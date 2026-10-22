package com.kswallet.feature.categories.data.mapper

import com.kswallet.core.database.entity.CategoryEntity
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import kotlin.time.Instant

/**
 * Data <-> domain translation. The database stores strings and longs; the domain
 * speaks enums and value classes. Doing this in one place is what lets the schema change
 * without touching a single use case.
 */
fun CategoryEntity.toDomain(): Category = Category(
    id = CategoryId(id),
    name = name,
    type = CategoryType.valueOf(type),
    iconKey = iconKey,
    colorArgb = colorArgb,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = if (id.isSaved) id.value else 0L,
    name = name,
    type = type.name,
    iconKey = iconKey,
    colorArgb = colorArgb,
    createdAt = createdAt.toEpochMilliseconds(),
)
