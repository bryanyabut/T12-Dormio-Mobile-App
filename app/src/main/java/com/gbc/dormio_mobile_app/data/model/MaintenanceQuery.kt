package com.gbc.dormio_mobile_app.data.model

data class MaintenanceQuery(
    val search: String? = null,
    val status: String? = null,
    val urgency: String? = null,
    val page: Int = 1,
    val limit: Int = 20,
    val sort: String = "createdAt",
    val order: String = "desc"
)
