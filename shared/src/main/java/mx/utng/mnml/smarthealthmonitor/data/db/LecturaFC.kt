package mx.utng.mnml.smarthealthmonitor.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "lecturas_fc")
data class LecturaFC(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bpm: Int,
    val estado: String = if (bpm in 60..100) "Normal" else if (bpm < 60) "FC Baja" else "FC Alta",
    val dispositivo: String = "app", // wear | app | tv
    val hora: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    @ColumnInfo(name = "sincronizado")
    val sincronizado: Boolean = false // false = pendiente de sync
) {
    val esNormal: Boolean get() = estado == "Normal"
}