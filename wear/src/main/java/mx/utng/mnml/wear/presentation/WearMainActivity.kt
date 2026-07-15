package mx.utng.mnml.wear.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository
import mx.utng.mnml.wear.mqtt.MqttWearPublisher
import mx.utng.mnml.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var wearDataSender: WearDataSender
    private var isRegistered = false
    private val neonRepo = mx.utng.mnml.wear.data.WearNeonRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Inicializar el repositorio compartido en el módulo wear
        SmartHealthRepository.init(applicationContext)

        wearDataSender = WearDataSender(applicationContext)

        // Conectar a MQTT en hilo de fondo
        lifecycleScope.launch(Dispatchers.IO) {
            MqttWearPublisher.connect()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        val permissions = arrayOf(
            android.Manifest.permission.BODY_SENSORS,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        )

        val sensorsGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.BODY_SENSORS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (sensorsGranted) {
            registerHealthServices()
        } else {
            permissionLauncher.launch(permissions)
        }

        setContent {
            SmartHealthWearTheme {
                // Reemplazado con WearNavGraph
                SmartHealthWearNavGraph()
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val sensorsGranted = result[android.Manifest.permission.BODY_SENSORS] == true
        if (sensorsGranted) {
            registerHealthServices()
        } else {
            Log.e("WearMainActivity", "Permiso de sensores corporales denegado")
        }
    }

    private fun registerHealthServices() {
        if (isRegistered) return
        isRegistered = true

        // Registrar el HealthDataService para monitorear el sensor de FC en segundo plano
        lifecycleScope.launch {
            try {
                HealthDataService.registrar(applicationContext)
                Log.d("WearMainActivity", "Servicio pasivo registrado")
            } catch (e: Exception) {
                Log.e("WearMainActivity", "Error al registrar servicio pasivo", e)
            }
        }

        // Registrar el SensorEventListener para actualización en tiempo real e inmediata
        heartRateSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("WearMainActivity", "Sensor de ritmo cardíaco registrado")
        } ?: Log.w("WearMainActivity", "El dispositivo no tiene sensor de ritmo cardíaco")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values[0].toInt()
            Log.d("WearMainActivity", "FC recibida en tiempo real (SensorManager): $bpm bpm")
            lifecycleScope.launch {
                // Actualizar el repositorio local para que la UI del reloj cambie de inmediato
                SmartHealthRepository.actualizarFC(bpm)
                // Enviar el dato en tiempo real a la app del celular
                wearDataSender.enviarFC(bpm)

                // Publicar a MQTT
                val estado = when {
                    bpm < 60 -> "FC Baja"
                    bpm > 100 -> "FC Alta"
                    else -> "Normal"
                }
                withContext(Dispatchers.IO) {
                    MqttWearPublisher.publishFC(bpm, estado)
                }

                // Publicar a Neon en hilo IO
                launch(Dispatchers.IO) {
                    runCatching { neonRepo.publicarLectura(bpm, estado) }
                        .onFailure { Log.w("WEAR", "Sin red para Neon: ${it.message}") }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            Log.d("WearMainActivity", "SensorEventListener desregistrado")
        }
    }
}