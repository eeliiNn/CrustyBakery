package com.crustybakery.mobile.data.local

import android.content.Context
import com.crustybakery.mobile.data.model.Cliente
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val preferences =
        context.getSharedPreferences("crusty_bakery_session", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun guardarCliente(cliente: Cliente) {
        preferences.edit()
            .putString(KEY_CLIENTE, gson.toJson(cliente))
            .apply()
    }

    fun obtenerCliente(): Cliente? {
        val json = preferences.getString(KEY_CLIENTE, null) ?: return null
        return runCatching { gson.fromJson(json, Cliente::class.java) }.getOrNull()
    }

    fun cerrarSesion() {
        preferences.edit().remove(KEY_CLIENTE).apply()
    }

    companion object {
        private const val KEY_CLIENTE = "cliente"
    }
}
