package mx.utng.mnml.wear.presentation

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await // Necesario para el .await() de ListenableFuture

import mx.utng.mnml.wear.mqtt.MqttWearPublisher
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository

class HealthDataService : PassiveListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var wearDataSender: WearDataSender
    private val neonRepo = mx.utng.mnml.wear.data.WearNeonRepository()

    override fun onCreate() {
        super.onCreate()
        wearDataSender = WearDataSender(this) // S6: MessageClient
        scope.launch {
            MqttWearPublisher.connect()
        }
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        val fcDataPoints = dataPoints.getData(DataType.HEART_RATE_BPM)

        fcDataPoints.forEach { dataPoint ->
            if (dataPoint is SampleDataPoint<Double>) {
                val bpm = dataPoint.value.toInt()
                android.util.Log.d("MQTT_WEAR", "HealthDataService: Nueva FC recibida de Health Services: $bpm bpm")
                scope.launch {
                    // Actualizar el repositorio local para que la UI del reloj cambie
                    SmartHealthRepository.actualizarFC(bpm)
                    
                    wearDataSender.enviarFC(bpm)

                    // Publicar a MQTT
                    val estado = when {
                        bpm < 60 -> "FC Baja"
                        bpm > 100 -> "FC Alta"
                        else -> "Normal"
                    }
                    MqttWearPublisher.publishFC(bpm, estado)

                    // Publicar a Neon
                    launch(Dispatchers.IO) {
                        runCatching { neonRepo.publicarLectura(bpm, estado) }
                            .onFailure { android.util.Log.w("WEAR", "Sin red para Neon: ${it.message}") }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        suspend fun registrar(context: Context) {
            val hsClient = HealthServices.getClient(context)
            val passiveClient = hsClient.passiveMonitoringClient

            val config = PassiveListenerConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .setShouldUserActivityInfoBeRequested(true)
                .build()

            passiveClient.setPassiveListenerServiceAsync(
                HealthDataService::class.java,
                config
            ).await()
        }
    }
}