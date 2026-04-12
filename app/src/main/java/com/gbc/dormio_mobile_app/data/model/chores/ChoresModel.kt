package com.gbc.dormio_mobile_app.data.model.chores

data class DashboardData(
    val greeting: String,
    val todayChores: List<Chore>,
    val stats: ChoreStats
)

data class Chore(
    val id: Int,
    val name: String,
    val description: String?,
    val dueDate: String,
    val status: String,
    val isDueToday: Boolean,
    val isOverdue: Boolean,
    val assignedUsers: List<AssignedUser>
)

data class AssignedUser(
    val id: Int,
    val initials: String
)

data class ChoreStats(
    val choresLeft: Int,
    val totalChores: Int,
    val completedChores: Int,
    val progressMessage: String,
    val percentComplete: Float
)