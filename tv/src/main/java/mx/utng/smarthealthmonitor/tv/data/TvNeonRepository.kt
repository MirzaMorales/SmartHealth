package mx.utng.smarthealthmonitor.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.utng.mnml.smarthealthmonitor.data.remote.NeonClient
import mx.utng.mnml.smarthealthmonitor.data.remote.NeonRequest
import mx.utng.mnml.smarthealthmonitor.data.remote.LecturaFcDto

class TvNeonRepository {
    
    /** Obtener historial completo de los 3 dispositivos */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """
                        SELECT id, bpm, estado, dispositivo, hora, created_at
                        FROM lecturas_fc
                        ORDER BY created_at DESC
                        LIMIT $1
                    """.trimIndent(),
                    params = listOf(limite)
                )
            ).rows
        }

    /** Estadísticas por dispositivo */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """
                        SELECT 0 AS id, dispositivo,
                               CAST(ROUND(AVG(bpm)) AS INTEGER) AS bpm,
                               'Promedio' AS estado,
                               MAX(hora) AS hora
                        FROM lecturas_fc
                        GROUP BY dispositivo
                    """.trimIndent()
                )
            ).rows
        }

    // --- Consultas SQL Avanzadas (Reto Extra) ---

    /** Alertas de FC fuera de rango (últimas 24 horas) */
    suspend fun obtenerAlertasRango(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """
                        SELECT id, bpm, estado, dispositivo, hora, created_at
                        FROM lecturas_fc
                        WHERE (bpm < 60 OR bpm > 100)
                          AND created_at > NOW() - INTERVAL '24 hours'
                        ORDER BY created_at DESC
                    """.trimIndent()
                )
            ).rows
        }

    /** Promedio de FC por hora del día */
    suspend fun obtenerPromedioPorHora(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """
                        SELECT 0 AS id, 
                               CAST(EXTRACT(HOUR FROM created_at) AS VARCHAR(10)) AS dispositivo,
                               CAST(ROUND(AVG(bpm)) AS INTEGER) AS bpm,
                               'Promedio Hora' AS estado,
                               CAST(COUNT(*) AS VARCHAR(10)) AS hora
                        FROM lecturas_fc
                        GROUP BY EXTRACT(HOUR FROM created_at)
                        ORDER BY EXTRACT(HOUR FROM created_at)
                    """.trimIndent()
                )
            ).rows
        }

    /** Lectura más reciente de cada dispositivo */
    suspend fun obtenerMasRecientePorDispositivo(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """
                        SELECT DISTINCT ON (dispositivo)
                               id, bpm, estado, dispositivo, hora, created_at
                        FROM lecturas_fc
                        ORDER BY dispositivo, created_at DESC
                    """.trimIndent()
                )
            ).rows
        }
}
