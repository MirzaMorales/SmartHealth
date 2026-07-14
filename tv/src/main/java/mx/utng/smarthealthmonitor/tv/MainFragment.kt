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
                val detail = DetailFragment.newInstance(item.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, detail)
                    .addToBackStack(null)  // Back regresa al BrowseFragment
                    .commit()
            }
        }
    }

    private fun observarDatos() {
        // Observar historial de Room y actualizar la fila
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historial.collect { lecturas ->
                    histAdapter.clear()
                    lecturas.forEach { histAdapter.add(it) }
                }
            }
        }

        // Observar FC actual y actualizar el encabezado de la primera fila
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fc.collect { fcVal ->
                    if (fcVal > 0) {
                        val newHeader = HeaderItem(0, "Historial FC - Último: $fcVal bpm")
                        val newRow = ListRow(newHeader, histAdapter)
                        if (rowsAdapter.size() > 0) {
                            rowsAdapter.replace(0, newRow)
                        } else {
                            rowsAdapter.add(newRow)
                        }
                    }
                }
            }
        }
    }

    private fun cargarFilas() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // Fila historial con adapter reactivo
        histAdapter = ArrayObjectAdapter(FCCardPresenter())
        rowsAdapter.add(ListRow(HeaderItem("Historial FC"), histAdapter))

        this.adapter = rowsAdapter
    }
}
