package mx.utng.smarthealthmonitor.tv

import android.os.Bundle
import android.view.View
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.PlaybackSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.leanback.LeanbackPlayerAdapter

class PlaybackFragment : PlaybackSupportFragment() {

    private lateinit var player: ExoPlayer

    companion object {
        private const val UPDATE_DELAY_MS = 16
        const val ARG_URL = "media_url"
        const val ARG_TITLE = "media_title"

        fun newInstance(url: String, title: String = "Alerta"): PlaybackFragment =
            PlaybackFragment().apply {
                arguments = Bundle().also {
                    it.putString(ARG_URL, url)
                    it.putString(ARG_TITLE, title)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url   = arguments?.getString(ARG_URL)   ?: return
        val title = arguments?.getString(ARG_TITLE) ?: ""

        // 1. Crear el motor de reproducción
        player = ExoPlayer.Builder(requireContext()).build()

        // 2. Conectar con la UI de Leanback
        val adapter = LeanbackPlayerAdapter(
            requireContext(), player, UPDATE_DELAY_MS
        )
        val glue = PlaybackTransportControlGlue(requireContext(), adapter).apply {
            this.title    = title
            this.subtitle = "SmartHealth Monitor"
            host = PlaybackSupportFragmentGlueHost(this@PlaybackFragment)
            playWhenPrepared()
        }

        // 3. Cargar y reproducir el media
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
    }

    // ⚠️ SIEMPRE liberar ExoPlayer — error crítico olvidarlo
    override fun onDestroyView() {
        super.onDestroyView()
        if (::player.isInitialized) player.release()
    }
}
