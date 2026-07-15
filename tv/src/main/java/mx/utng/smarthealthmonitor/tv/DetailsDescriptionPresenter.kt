package mx.utng.smarthealthmonitor.tv

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: ViewHolder,
        item: Any
    ) {
        val lectura = item as LecturaFC

        // Título: valor de FC
        viewHolder.title.text = "${lectura.bpm} bpm"

        // Subtítulo: estado de salud
        viewHolder.subtitle.text = if (lectura.esNormal)
            "✓ Frecuencia normal (${lectura.estado})"
        else
            "⚠ ${lectura.estado} — consulta al médico"

        // Cuerpo: hora, dispositivo e ID
        viewHolder.body.text =
            "Dispositivo: ${lectura.dispositivo.uppercase()}\n" +
            "Registrado a las ${lectura.hora}\n" +
            "ID de lectura: ${lectura.id}"
    }
}
