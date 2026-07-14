package mx.utng.mnml.smarthealthmonitor

import android.app.Application
import com.google.android.gms.cast.framework.CastContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository
import mx.utng.mnml.smarthealthmonitor.mqtt.MqttAppService

class SmartHealthApp : Application() {

    lateinit var mqttService: MqttAppService

    override fun onCreate() {
        super.onCreate()

        // Inicializar Room al abrir la aplicación
        SmartHealthRepository.init(this)

        // Inicializar MQTT en segundo plano
        mqttService = MqttAppService(this)
        CoroutineScope(Dispatchers.IO).launch {
            mqttService.connect()
        }

        // Inicializar Cast SDK al arrancar la app
        try {
            CastContext.getSharedInstance(this)  // lazy init
        } catch (e: Exception) {
            // Cast no disponible en este dispositivo (emulador sin GMS)
            android.util.Log.w("Cast", "Cast not available: ${e.message}")
        }
    }
}