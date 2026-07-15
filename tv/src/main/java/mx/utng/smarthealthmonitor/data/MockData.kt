package mx.utng.smarthealthmonitor.data

import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC

object MockData {
    val historialFC = listOf(
        LecturaFC(id = 1, bpm = 78, hora = "11:00"),
        LecturaFC(id = 2, bpm = 82, hora = "10:30"),
        LecturaFC(id = 3, bpm = 76, hora = "10:00"),
        LecturaFC(id = 4, bpm = 95, hora = "09:30"),
        LecturaFC(id = 5, bpm = 71, hora = "09:00"),
        LecturaFC(id = 6, bpm = 80, hora = "08:30"),
        LecturaFC(id = 7, bpm = 74, hora = "08:00")
    )
}
