package mx.utng.mnml.smarthealthmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository
import mx.utng.mnml.smarthealthmonitor.ui.components.FilaHistorial
import mx.utng.mnml.smarthealthmonitor.ui.components.TarjetaDato
import mx.utng.mnml.smarthealthmonitor.ui.theme.SmartHealthMonitorTheme
import mx.utng.mnml.smarthealthmonitor.ui.viewmodel.DashboardViewModel
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onHistorialClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    // collectAsState() convierte StateFlow en State de Compose
    val fc by viewModel.fc.collectAsState()
    val pasos by viewModel.pasos.collectAsState()
    val historial by viewModel.historial.collectAsState(initial = emptyList())

    // ── Estado del diálogo y Snackbar ──────────────────────
    var mostrarAlerta by remember { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── Diálogo condicional ────────────────────────────────
    if (mostrarAlerta) {
        AlertaScreen(
            fc = fc,
            onDismiss = { mostrarAlerta = false },
            onConfirmar = {
                mostrarAlerta = false
                scope.launch {
                    snackbarHost.showSnackbar(
                        message = "Alerta enviada a tus contactos de emergencia",
                        duration = SnackbarDuration.Long
                    )
                }
            }
        )
    }

    SmartHealthMonitorTheme {
        Scaffold(
            // ── Snackbar host en el Scaffold ───────────────
            snackbarHost = { SnackbarHost(hostState = snackbarHost) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SmartHealth",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        AndroidView(
                            factory = { ctx ->
                                val themedContext = android.view.ContextThemeWrapper(
                                    ctx,
                                    androidx.mediarouter.R.style.Theme_MediaRouter
                                )
                                MediaRouteButton(themedContext).apply {
                                    CastButtonFactory.setUpMediaRouteButton(themedContext, this)
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarAlerta = true },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Enviar alerta de emergencia",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Tarjeta FC ────────────────────────────
                item {
                    TarjetaDato(
                        valor = "$fc",
                        unidad = "bpm",
                        label = "Frecuencia cardíaca",
                        colorValor = MaterialTheme.colorScheme.error,
                        colorUnidad = MaterialTheme.colorScheme.error.copy(alpha = 0.7f) // Ejemplo: rojo suave
                    )
                }

                // ── Tarjeta Pasos ─────────────────────────
                item {
                    TarjetaDato(
                        valor = "%,d".format(pasos),
                        unidad = "pasos",
                        label = "Pasos del día",
                        colorValor = MaterialTheme.colorScheme.primary
                    )
                }

                // ── Encabezado historial ──────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial reciente",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = onHistorialClick) {
                            Text("Ver todo")
                        }
                    }
                }

                // ── Lista del historial ───────────────────
                items(historial, key = { it.id }) { lectura ->
                    FilaHistorial(lectura = lectura)
                }

                // ── Botón de simulación (SOLO PARA DEBUG) ──
                item {
                    OutlinedButton(
                        onClick = {
                            val fcSimulado = (60..110).random()

                            CoroutineScope(Dispatchers.IO).launch {
                                SmartHealthRepository.actualizarFC(fcSimulado)
                            }

                            SmartHealthRepository.actualizarPasos((3000..8000).random())
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simular dato del wearable (DEBUG)")
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Dashboard - Light",
    showSystemUi = true,
    device = "id:pixel_6"
)
@Preview(
    showBackground = true,
    name = "Dashboard - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DashboardScreenPreview() {
    SmartHealthMonitorTheme {
        DashboardScreen()
    }
}