package mx.utng.smarthealthmonitor.tv

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC
import mx.utng.mnml.smarthealthmonitor.data.db.SmartHealthDB

class DetailFragment : DetailsSupportFragment(),
    OnActionClickedListener {

    private var lectura: LecturaFC? = null

    companion object {
        const val ARG_LECTURA_ID = "lectura_id"
        const val ACTION_PLAY    = 1L
        const val ACTION_BACK    = 2L
        const val TEST_AUDIO_URL = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

        fun newInstance(
            lecturaId: Int,
            bpm: Int,
            estado: String,
            dispositivo: String,
            hora: String
        ): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().also {
                    it.putInt(ARG_LECTURA_ID, lecturaId)
                    it.putInt("bpm", bpm)
                    it.putString("estado", estado)
                    it.putString("dispositivo", dispositivo)
                    it.putString("hora", hora)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt(ARG_LECTURA_ID) ?: return
        val bpm = arguments?.getInt("bpm") ?: 0
        val estado = arguments?.getString("estado") ?: "Normal"
        val dispositivo = arguments?.getString("dispositivo") ?: "app"
        val hora = arguments?.getString("hora") ?: ""

        val detailLectura = LecturaFC(
            id = id,
            bpm = bpm,
            estado = estado,
            dispositivo = dispositivo,
            hora = hora,
            sincronizado = true
        )
        lectura = detailLectura
        construirDetalle(detailLectura)
    }

    private fun construirDetalle(lectura: LecturaFC) {
        val selector = ClassPresenterSelector()

        val dpPresenter = FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter()
        )
        dpPresenter.setOnActionClickedListener(this)
        selector.addClassPresenter(DetailsOverviewRow::class.java, dpPresenter)

        val row = DetailsOverviewRow(lectura)
        // Ícono de corazón como imagen del detalle
        val iconRes = if (lectura.esNormal)
            android.R.drawable.ic_menu_compass  // placeholder OK
        else
            android.R.drawable.ic_dialog_alert  // placeholder error
        row.imageDrawable = ContextCompat.getDrawable(requireContext(), iconRes)

        // Botones de acción
        val actions = ArrayObjectAdapter()
        actions.add(Action(ACTION_PLAY, "▶ Reproducir alerta"))
        actions.add(Action(ACTION_BACK, "← Volver al historial"))
        row.actionsAdapter = actions

        val adapter = ArrayObjectAdapter(selector)
        adapter.add(row)
        this.adapter = adapter
    }

    override fun onActionClicked(action: Action) {
        when (action.id) {
            ACTION_PLAY -> {
                val currentLectura = lectura ?: return
                val playback = PlaybackFragment.newInstance(
                    url   = TEST_AUDIO_URL,
                    title = "Alerta FC ${currentLectura.bpm} bpm"
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, playback)
                    .addToBackStack(null)
                    .commit()
            }
            ACTION_BACK -> {
                @Suppress("DEPRECATION")
                requireActivity().onBackPressed()
            }
        }
    }
}
