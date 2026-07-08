package mx.utng.smarthealthmonitor.tv

import android.app.Application
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository

class TvApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartHealthRepository.init(this)
    }
}
