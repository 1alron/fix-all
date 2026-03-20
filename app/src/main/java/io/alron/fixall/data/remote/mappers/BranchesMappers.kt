package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.BranchDto
import io.alron.fixall.domain.model.Branch

fun BranchDto.toDomain() = Branch(
    id = id,
    address = address,
    phone = phone,
    openingHours = opening_hours,
    photoUrl = photo_url,
)