package mx.utng.mnml.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Asegúrate de importar tu tema si está en otro paquete
import mx.utng.mnml.wear.presentation.theme.SmartHealthWearTheme

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nota: Si aún necesitas registrar tu HealthDataService como en el código
        // anterior, recuerda agregarlo aquí antes del setContent.

        setContent {
            SmartHealthWearTheme {
                // TODO Ej.02: reemplazar con WearNavGraph
                // WearDashboardScreen()
            }
        }
    }
}