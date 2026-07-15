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
import mx.utng.smarthealthmonitor.tv.data.TvNeonRepository
import mx.utng.smarthealthmonitor.tv.data.TvUiState
import mx.utng.smarthealthmonitor.tv.data.toLecturaFC
import mx.utng.smarthealthmonitor.tv.mqtt.MqttTvSubscriber
 
class TvViewModel(application: Application) : AndroidViewModel(application) {
 
    private val context = application.applicationContext
    private val neonRepo = TvNeonRepository()

    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    // FC actual del wearable (o 0 si no hay dato)
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .stateIn(viewModelScope,
                 SharingStarted.WhileSubscribed(5_000), 0)

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(context, mqttFlow)

    init {
        cargarDatos()

        // Conectar a MQTT en hilo de fondo
        viewModelScope.launch(Dispatchers.IO) {
            mqttSubscriber.connect()
        }

        // Observar mensajes MQTT y actualizar la UI/repositorio
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                // Actualizar la FC en tiempo real
                SmartHealthRepository.actualizarFC(tvMsg.bpm)
                // Refrescar los datos de Neon para mostrar el registro que acaba de llegar
                cargarDatos()
            }
        }
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                val stats = neonRepo.obtenerEstadisticas()
                
                _state.update {
                    it.copy(
                        lecturas = lecturas.map { dto -> dto.toLecturaFC() },
                        estadisticas = stats.map { dto -> dto.toLecturaFC() },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun refresh() {
        cargarDatos()
    }

    override fun onCleared() {
        super.onCleared()
        // Desconectar MQTT en hilo de fondo
        viewModelScope.launch(Dispatchers.IO) {
            mqttSubscriber.disconnect()
        }
    }
}
