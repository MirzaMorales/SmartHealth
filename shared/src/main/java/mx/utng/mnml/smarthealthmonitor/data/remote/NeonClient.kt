package mx.utng.mnml.smarthealthmonitor.data.remote

import mx.utng.mnml.shared.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NeonClient {
    private const val BASE_URL = "https://${BuildConfig.NEON_HOST}/"
    val AUTH_HEADER = "Bearer ${BuildConfig.NEON_API_KEY}"
    
    // Armar la cadena de conexión usando las credenciales seguras de local.properties
    val CONN_STRING = "postgresql://${BuildConfig.NEON_USER}:${BuildConfig.NEON_PASSWORD}@${BuildConfig.NEON_HOST}/${BuildConfig.NEON_DB}?sslmode=require"

    val api: NeonApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NeonApiService::class.java)
    }
}
