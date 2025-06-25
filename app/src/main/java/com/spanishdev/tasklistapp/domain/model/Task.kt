package com.spanishdev.tasklistapp.domain.model

data class Task(
    val id: Long,
    val name: String,
    val description: String,
    val status: Status,
)

enum class Status {
    Pending,
    InProgress,
    Done,
    Cancelled
}