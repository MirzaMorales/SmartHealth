package mx.utng.smarthealthmonitor.tv.data

import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC
import mx.utng.mnml.smarthealthmonitor.data.remote.LecturaFcDto

data class TvUiState(
    val lecturas: List<LecturaFC> = emptyList(),
    val estadisticas: List<LecturaFC> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/** Convierte un DTO de Neon a la entidad interna LecturaFC */
fun LecturaFcDto.toLecturaFC(): LecturaFC {
    return LecturaFC(
        id = this.id,
        bpm = this.bpm,
        estado = this.estado,
        dispositivo = this.dispositivo,
        hora = this.hora,
        sincronizado = true
    )
}
