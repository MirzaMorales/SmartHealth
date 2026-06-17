package mx.utng.mnml.smarthealthmonitor.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

// Revisa que la ruta de estos imports coincida con la ubicación de tus archivos de BD
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFCDao
import mx.utng.mnml.smarthealthmonitor.data.db.SmartHealthDB

/** * Repositorio singleton que centraliza los datos de salud.
 * El WearListenerService escribe aquí.
 * El ViewModel lee de aquí.
 */
object SmartHealthRepository {

    // FC actual del wearable (bpm)
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    // Pasos del día actual (se mantiene de tu código original)
    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    // Variable para manejar la base de datos local
    private var dao: LecturaFCDao? = null

    // Inicializa la base de datos
    fun init(context: Context) {
        dao = SmartHealthDB.getDatabase(context).lecturaDao()
    }

    // Actualiza la FC en la UI y la guarda en Room al mismo tiempo
    suspend fun actualizarFC(bpm: Int) {
        _fcFlow.value = bpm

        // Persistir en Room automáticamente
        dao?.insertar(LecturaFC(valorBpm = bpm))
    }

    // Se mantiene tu función original para los pasos
    fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }

    // Flow reactivo del historial desde Room
    fun obtenerHistorial(): Flow<List<LecturaFC>> {
        return dao?.obtenerUltimas() ?: emptyFlow()
    }
}