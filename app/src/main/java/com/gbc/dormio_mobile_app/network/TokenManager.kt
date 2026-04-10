package com.gbc.dormio_mobile_app.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import java.util.Base64

object TokenManager {

    private const val PREFS_NAME = "dormio_prefs"
    private const val KEY_JWT_TOKEN = "jwt_token"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_TOKEN_EXPIRY = "jwt_token_expiry"
    const val ACTION_SESSION_EXPIRED = "com.gbc.dormio_mobile_app.SESSION_EXPIRED"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String, role: String) {
        val expiry = parseExpFromJwt(token)
        getPrefs(context).edit()
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_ROLE, role)
            .putLong(KEY_TOKEN_EXPIRY, expiry)
            .apply()
    }

    fun getUserRole(context: Context): String {
        return getPrefs(context).getString(KEY_USER_ROLE, "STUDENT") ?: "STUDENT"
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_JWT_TOKEN, null)
    }

    fun isTokenExpired(context: Context): Boolean {
        val expiry = getPrefs(context).getLong(KEY_TOKEN_EXPIRY, 0L)
        if (expiry == 0L) return true
        return System.currentTimeMillis() / 1000 >= expiry
    }

    fun clearToken(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_JWT_TOKEN)
            .remove(KEY_USER_ROLE)
            .remove(KEY_TOKEN_EXPIRY)
            .apply()
    }

    fun notifySessionExpired(context: Context) {
        clearToken(context)
        val intent = Intent(ACTION_SESSION_EXPIRED)
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
    }

    private fun parseExpFromJwt(token: String): Long {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return 0L
            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            val json = JSONObject(payload)
            json.optLong("exp", 0L)
        } catch (e: Exception) {
            Log.e("TokenManager", "Failed to parse JWT expiry", e)
            0L
        }
    }
}
