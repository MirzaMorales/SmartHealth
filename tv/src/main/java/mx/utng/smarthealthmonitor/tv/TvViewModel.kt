package mx.utng.smarthealthmonitor.tv
 
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository
import mx.utng.mnml.smarthealthmonitor.mqtt.TvMessage
import mx.utng.smarthealthmonitor.tv.mqtt.MqttTvSubscriber
 
class TvViewModel(application: Application) : AndroidViewModel(application) {
 
    private val context = application.applicationContext

    // FC actual del wearable (o 0 si no hay dato)
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(viewModelScope,
                 SharingStarted.WhileSubscribed(5_000), 0)
 
    // Historial de lecturas desde Room DAO
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                     SharingStarted.WhileSubscribed(5_000),
                     emptyList())

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(context, mqttFlow)

    init {
        // Conectar a MQTT en hilo de fondo
        viewModelScope.launch(Dispatchers.IO) {
            mqttSubscriber.connect()
        }

        // Observar mensajes MQTT y actualizar la UI/repositorio
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                // Actualizar el repositorio local de la TV para guardar en Room y actualizar fcFlow
                SmartHealthRepository.actualizarFC(tvMsg.bpm)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Desconectar MQTT en hilo de fondo
        viewModelScope.launch(Dispatchers.IO) {
            mqttSubscriber.disconnect()
        }
    }
}
