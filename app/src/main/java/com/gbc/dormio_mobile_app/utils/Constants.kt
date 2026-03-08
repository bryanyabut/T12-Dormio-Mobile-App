package com.gbc.dormio_mobile_app.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
    const val CONNECT_TIMEOUT: Long = 10
    const val READ_TIMEOUT: Long = 10
    const val WRITE_TIMEOUT: Long = 10
    const val API_AUTH_LOGIN = "auth/login"
    const val API_AUTH_REGISTER = "auth/register"
    const val API_AUTH_LOGOUT = "auth/logout"
    const val API_AUTH_VERIFY = "auth/verify"
    const val API_AUTH_DEVICE_TOKEN = "auth/device-token"

    // maintenance endpoints
    // STUDENT ENDPOINTS
    const val API_MAINTENANCE_REQUESTS_CREATE = "maintenance/create"
    const val API_MAINTENANCE_MY_REQUESTS = "maintenance/myReq"
        const val API_MAINTENANCE_REQUEST_DETAIL_STUDENT = "maintenance/myReq"
    const val API_MAINTENANCE_REQUEST_UPDATE_STUDENT = "maintenance/student/:id"

    // ADMIN ENDPOINTS
    const val API_MAINTENANCE_REQUESTS_ALL = "maintenance/"
    const val API_MAINTENANCE_REQUEST_DETAIL_ADMIN = "maintenance/:id"
    const val API_MAINTENANCE_REQUEST_UPDATE_STATUS = "maintenance/:id/status"

    //BOTH USER ENDPOINTS
    const val API_MAINTENANCE_REQUEST_DELETE = "maintenance/:id"
    // end of maintenance endpoints


}