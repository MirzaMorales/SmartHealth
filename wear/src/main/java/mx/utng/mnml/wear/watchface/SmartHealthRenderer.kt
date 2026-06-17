package mx.utng.mnml.wear.watchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository

class SmartHealthRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    complicationSlotsManager: ComplicationSlotsManager,
    currentUserStyleRepository: CurrentUserStyleRepository,
    interactiveDrawModeUpdateDelayMillis: Long
) : Renderer.CanvasRenderer2<Renderer.SharedAssets>(
    surfaceHolder, currentUserStyleRepository, watchState,
    CanvasType.HARDWARE, interactiveDrawModeUpdateDelayMillis,
    true
), SensorEventListener {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    private val paintHora = Paint().apply {
        color     = Color.WHITE
        textSize  = 72f
        isAntiAlias = true
        typeface  = Typeface.DEFAULT_BOLD
    }
    private val paintFC = Paint().apply {
        color     = Color.RED
        textSize  = 30f
        isAntiAlias = true
    }
    private val paintSub = Paint().apply {
        color     = Color.GRAY
        textSize  = 22f
        isAntiAlias = true
    }

    init {
        // Inicializar el repositorio
        SmartHealthRepository.init(context)

        // Registrar el sensor de ritmo cardíaco
        heartRateSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Observar cambios en el flujo para redibujar de inmediato
        scope.launch {
            SmartHealthRepository.fcFlow.collect {
                invalidate()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values[0].toInt()
            scope.launch {
                SmartHealthRepository.actualizarFC(bpm)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        scope.cancel()
        super.onDestroy()
    }

    override suspend fun createSharedAssets(): Renderer.SharedAssets =
        object : Renderer.SharedAssets { override fun onDestroy() {} }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets
    ) {
        // Fondo negro — ahorra batería en modo AOD
        canvas.drawColor(Color.BLACK)

        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()

        // Hora digital centrada
        val hora = String.format("%02d:%02d", zonedDateTime.hour, zonedDateTime.minute)
        val tw = paintHora.measureText(hora)
        canvas.drawText(hora, cx - tw/2, cy - 10f, paintHora)

        // Segundos (pequeño debajo)
        val seg = String.format("%02d", zonedDateTime.second)
        canvas.drawText(seg, cx - 18f, cy + 30f, paintSub)

        // FC desde SmartHealthRepository
        val fc = SmartHealthRepository.fcFlow.value
        if (fc > 0) {
            val fcStr = "❤ $fc bpm"
            val fcW = paintFC.measureText(fcStr)
            canvas.drawText(fcStr, cx - fcW/2, cy + 70f, paintFC)
        }
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: Renderer.SharedAssets
    ) {
        canvas.drawColor(renderParameters.highlightLayer!!.backgroundTint)
    }
}
