package io.alron.fixall.data.remote.mappers

import io.alron.fixall.data.remote.dto.BranchDto
import io.alron.fixall.data.remote.dto.ReviewDto
import io.alron.fixall.data.remote.dto.ServiceDto
import io.alron.fixall.data.remote.dto.WorkingHourDto
import io.alron.fixall.domain.model.Branch
import io.alron.fixall.domain.model.Review
import io.alron.fixall.domain.model.Service
import io.alron.fixall.domain.model.WorkingHour

fun BranchDto.toDomain() = Branch(
    id = id,
    address = address,
    phone = phone,
    openingHours = openingHours,
    photoUrl = photoUrl,
    services = services?.map { it.toDomain() } ?: emptyList(),
    workingHours = workingHours?.map { it.toDomain() } ?: emptyList(),
    reviewsCount = reviewsCount ?: 0,
    averageRating = averageRating ?: 0.0,
    latestReviews = latestReviews?.map { it.toDomain() } ?: emptyList(),
    isOpenNow = isOpenNow ?: false
)

fun ServiceDto.toDomain() = Service(
    id = id,
    name = name,
    description = description,
    duration = duration,
    price = price,
    isActive = isActive
)

fun WorkingHourDto.toDomain() = WorkingHour(
    id = id,
    dayOfWeek = dayOfWeek,
    dayOfWeekDisplay = dayOfWeekDisplay,
    startTime = startTime,
    endTime = endTime,
    lunchStart = lunchStart,
    lunchEnd = lunchEnd,
    isWorking = isWorking
)

fun ReviewDto.toDomain() = Review(
    id = id,
    userName = userName,
    userAvatar = userAvatar,
    rating = rating,
    comment = comment,
    adminReply = adminReply,
    adminReplyAt = adminReplyAt,
    createdAt = createdAt
)
