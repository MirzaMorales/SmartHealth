package mx.utng.mnml.smarthealthmonitor.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LecturaFC::class],
    version = 2,
    exportSchema = false // cambiar a true en producción para manejar migraciones
)
abstract class SmartHealthDB : RoomDatabase() {

    abstract fun lecturaDao(): LecturaFCDao

    companion object {
        @Volatile
        private var INSTANCE: SmartHealthDB? = null

        fun getDatabase(context: Context): SmartHealthDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SmartHealthDB::class.java,
                    "smarthealthmonitor_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}