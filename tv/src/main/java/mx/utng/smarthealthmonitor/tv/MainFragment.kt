package mx.utng.smarthealthmonitor.tv

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment

/**
 * MainFragment para Android TV que hereda de BrowseSupportFragment.
 * Muestra el panel interactivo con las filas de contenido y la navegación lateral de Leanback.
 */
class MainFragment : BrowseSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUIElements()
    }

    private fun setupUIElements() {
        title = "SmartHealth Monitor TV"
        badgeDrawable = null
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        
        // Color del encabezado de navegación
        brandColor = resources.getColor(R.color.sh_primary, null)
    }
}
