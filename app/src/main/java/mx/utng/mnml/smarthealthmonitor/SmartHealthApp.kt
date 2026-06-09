package mx.utng.mnml.smarthealthmonitor

import android.app.Application
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository

class SmartHealthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Room al abrir la aplicación
        SmartHealthRepository.init(this)
    }
}