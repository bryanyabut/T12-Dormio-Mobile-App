package com.gbc.dormio_mobile_app.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREFS_NAME = "dormio_prefs"
    private const val KEY_JWT_TOKEN = "jwt_token"
    private const val KEY_USER_ROLE = "user_role"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String, role: String) {
        getPrefs(context).edit()
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_ROLE, role)
            .apply()
    }

    fun getUserRole(context: Context): String {
        return getPrefs(context).getString(KEY_USER_ROLE, "STUDENT") ?: "STUDENT"
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_JWT_TOKEN, null)
    }

    fun clearToken(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_JWT_TOKEN)
            .remove(KEY_USER_ROLE)
            .apply()
    }
}
