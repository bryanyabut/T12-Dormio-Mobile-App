package com.gbc.dormio_mobile_app.data.model.chores

import com.google.gson.annotations.SerializedName

data class DashboardData(
    val greeting: String,
    val todayChores: List<Chore>,
    val stats: ChoreStats
)

data class Chore(
    val id: Int,
    @SerializedName("choreName")
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

data class AddChoreRequest(
    val name: String,
    val description: String?,
    val dueDate: String,
    val assignedUserIds: List<Int>
)

data class UpdateChoreRequest(
    val name: String,
    val description: String?,
    val dueDate: String?,
    val assignedUserIds: List<Int>?
)
data class Housemate(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val initials: String = "",
    val isSelected: Boolean = false,
    val isCurrentUser: Boolean = false
)

data class ChoresDashboardUiState(
    val isLoading: Boolean = false,
    val actionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val data: DashboardData? = null
)

data class ChoreFormUiState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val initialChoreLoaded: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false,
    val housemates: List<Housemate> = emptyList(),
    val selectedDate: String = "",
    val assignedUserIds: List<Int> = emptyList(),
    val choreName: String = "",
    val description: String = ""
)