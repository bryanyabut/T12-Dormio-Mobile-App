package com.gbc.dormio_mobile_app.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
    const val CONNECT_TIMEOUT: Long = 10
    const val READ_TIMEOUT: Long = 10
    const val WRITE_TIMEOUT: Long = 10
    const val API_AUTH_LOGIN = "/api/v1/auth/login"
    const val API_AUTH_REGISTER = "/api/v1/auth/register"
    const val API_AUTH_LOGOUT = "/api/v1/auth/logout"
    const val API_AUTH_VERIFY = "/api/v1/auth/verify"
    const val API_AUTH_DEVICE_TOKEN = "/api/v1/auth/device-token"
}