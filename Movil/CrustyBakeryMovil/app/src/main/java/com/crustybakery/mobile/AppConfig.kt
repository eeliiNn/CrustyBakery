package com.crustybakery.mobile

object AppConfig {
    /**
     * Desarrollo con Android Emulator usando:
     * adb reverse tcp:5244 tcp:5244
     *
     * De esta forma el emulador accede a la API de la PC mediante localhost.
     * Debe terminar con "/" para Retrofit.
     */
    const val BASE_URL = "http://127.0.0.1:5244"
}
