package mx.utng.smarthealthmonitor.tv
 
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.db.LecturaFC

class MainFragment : BrowseSupportFragment() {

    private val viewModel: TvViewModel by viewModels()
    private lateinit var statAdapter: ArrayObjectAdapter
    private lateinit var histAdapter: ArrayObjectAdapter
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del BrowseFragment
        title        = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Color de la marca en el sidebar
        brandColor = resources.getColor(R.color.sh_primary, null)

        cargarFilas()
        observarDatos()

        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            if (item is LecturaFC) {
                val detail = DetailFragment.newInstance(
                    lecturaId = item.id,
                    bpm = item.bpm,
                    estado = item.estado,
                    dispositivo = item.dispositivo,
                    hora = item.hora
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, detail)
                    .addToBackStack(null)  // Back regresa al BrowseFragment
                    .commit()
            }
        }
    }

    private fun observarDatos() {
        // Observar estado del ViewModel (datos de Neon)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { uiState ->
                    if (!uiState.isLoading && uiState.error == null) {
                        statAdapter.clear()
                        uiState.estadisticas.forEach { statAdapter.add(it) }

                        histAdapter.clear()
                        uiState.lecturas.forEach { histAdapter.add(it) }
                    } else if (uiState.error != null) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Error al conectar con Neon: ${uiState.error}",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // Observar FC actual y actualizar el encabezado si se desea, o refrescar
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fc.collect { fcVal ->
                    if (fcVal > 0) {
                        viewModel.cargarDatos()
                    }
                }
            }
        }
    }

    private fun cargarFilas() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // Fila 1: Estado Actual
        statAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(0, "Estado Actual (3 dispositivos)"), statAdapter))

        // Fila 2: Historial Completo
        histAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem(1, "Historial Completo"), histAdapter))

        this.adapter = rowsAdapter
    }
}
