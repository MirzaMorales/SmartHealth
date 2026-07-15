package mx.utng.mnml.smarthealthmonitor.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaFCDao {

    @Query("SELECT * FROM lecturas_fc ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<LecturaFC>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(lectura: LecturaFC): Long

    @Query("SELECT * FROM lecturas_fc WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): LecturaFC?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lectura: LecturaFC)

    @Query("SELECT * FROM lecturas_fc WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizados(): List<LecturaFC>

    @Query("UPDATE lecturas_fc SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Long)

    @Query("SELECT COUNT(*) FROM lecturas_fc WHERE sincronizado = 0")
    fun contarPendientes(): Flow<Int>
}