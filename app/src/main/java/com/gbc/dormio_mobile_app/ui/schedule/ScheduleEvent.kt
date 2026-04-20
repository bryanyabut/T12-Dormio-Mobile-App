package com.gbc.dormio_mobile_app.ui.schedule

data class ScheduleEvent(
    val title: String,
    val startTime: String,
    val endTime: String,
    val category: EventCategory,
    val detail: String? = null,
    val recurrence: String? = null,
    val timeLabel: String = startTime
)

enum class EventCategory {
    PERSONAL, CLASS, WORK
}
