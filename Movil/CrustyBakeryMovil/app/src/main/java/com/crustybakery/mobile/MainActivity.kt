package com.crustybakery.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.crustybakery.mobile.navigation.AppNavigation
import com.crustybakery.mobile.ui.theme.CrustyBakeryTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CrustyBakeryApp.init(applicationContext)

        thread {
            try {
                val url = URL("http://127.0.0.1:5244/api/categorias")
                val connection = url.openConnection() as HttpURLConnection

                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"

                Log.d(
                    "PRUEBA_API",
                    "Código HTTP: ${connection.responseCode}"
                )

                val contenido =
                    connection.inputStream.bufferedReader().use { it.readText() }

                Log.d("PRUEBA_API", contenido)

                connection.disconnect()

            } catch (e: Exception) {
                Log.e("PRUEBA_API", "ERROR", e)
            }
        }

        setContent {
            CrustyBakeryTheme {
                AppNavigation()
            }
        }
    }
}