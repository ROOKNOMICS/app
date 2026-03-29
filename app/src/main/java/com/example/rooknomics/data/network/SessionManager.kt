package com.example.rooknomics.data.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME = "rooknomics_session"
        const val KEY_JWT_TOKEN = "jwt_token"
    }

    /**
     * Saves the raw JWT token extracted from secure routes.
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    /**
     * Retrieves the stored JWT token.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    /**
     * Clears session during Logout.
     */
    fun clearSession() {
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
    }
}
