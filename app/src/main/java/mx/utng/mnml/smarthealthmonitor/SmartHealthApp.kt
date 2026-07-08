package mx.utng.mnml.smarthealthmonitor

import android.app.Application
import com.google.android.gms.cast.framework.CastContext
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository

class SmartHealthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Room al abrir la aplicación
        SmartHealthRepository.init(this)

        // Inicializar Cast SDK al arrancar la app
        try {
            CastContext.getSharedInstance(this)  // lazy init
        } catch (e: Exception) {
            // Cast no disponible en este dispositivo (emulador sin GMS)
            android.util.Log.w("Cast", "Cast not available: ${e.message}")
        }
    }
}