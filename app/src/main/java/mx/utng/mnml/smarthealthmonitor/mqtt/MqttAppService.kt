package mx.utng.mnml.smarthealthmonitor.mqtt

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mx.utng.mnml.smarthealthmonitor.data.repository.SmartHealthRepository
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MqttAppService(private val context: Context) {

    private var client: MqttAsyncClient? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connect() {
        try {
            val uniqueClientId = "${MqttConfig.CLIENT_APP}-${java.util.UUID.randomUUID().toString().take(8)}"
            client = MqttAsyncClient(
                MqttConfig.BROKER_URL,
                uniqueClientId,
                MemoryPersistence()
            )

            val options = MqttConnectOptions().apply {
                userName = MqttConfig.USERNAME
                password = MqttConfig.PASSWORD.toCharArray()
                isCleanSession = true
                isAutomaticReconnect = true
                socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
            }

            // Callback para recibir mensajes entrantes
            client?.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    if (reconnect) {
                        try {
                            client?.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS)
                            android.util.Log.d("MQTT_APP", "✅ Re-suscrito a ${MqttConfig.TOPIC_FC} después de reconexión")
                        } catch (e: Exception) {
                            android.util.Log.e("MQTT_APP", "Error al re-suscribirse: ${e.message}")
                        }
                    }
                }

                override fun messageArrived(topic: String, msg: MqttMessage) {
                    when (topic) {
                        MqttConfig.TOPIC_FC -> handleFcMessage(msg)
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    android.util.Log.w("MQTT_APP", "Conexión perdida: ${cause?.message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            client?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(token: IMqttToken?) {
                    try {
                        // Suscribirse al topic de FC del reloj
                        client?.subscribe(MqttConfig.TOPIC_FC, MqttConfig.QOS)
                        android.util.Log.d("MQTT_APP", "✅ Conectado y suscrito a ${MqttConfig.TOPIC_FC}")
                    } catch (e: Exception) {
                        android.util.Log.e("MQTT_APP", "Error al suscribirse al conectar: ${e.message}")
                    }
                }

                override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                    android.util.Log.e("MQTT_APP", "❌ Error al conectar: ${ex?.message}")
                }
            })

        } catch (e: Exception) {
            android.util.Log.e("MQTT_APP", "Error al inicializar MqttAppService: ${e.message}")
        }
    }

    private fun handleFcMessage(msg: MqttMessage) {
        try {
            val payloadString = String(msg.payload)
            android.util.Log.d("MQTT_APP", "Mensaje recibido en Topic FC: $payloadString")
            val fcMsg = Json.decodeFromString<FcMessage>(payloadString)

            scope.launch {
                // 1. Actualizar el Repository (Room + local state)
                SmartHealthRepository.actualizarFC(fcMsg.bpm)
            }

            // 2. Re-publicar al topic TV con formato enriquecido
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val tvMsg = TvMessage(bpm = fcMsg.bpm, estado = fcMsg.estado, hora = hora)
            val tvPayload = Json.encodeToString(tvMsg).toByteArray()
            val tvMqtt = MqttMessage(tvPayload).apply {
                qos = MqttConfig.QOS
                isRetained = false // Deshabilitar retained
            }
            client?.publish(MqttConfig.TOPIC_TV, tvMqtt)
            android.util.Log.d("MQTT_APP", "   Re-publicado al TV: ${fcMsg.bpm} bpm")

        } catch (e: Exception) {
            android.util.Log.e("MQTT_APP", "Error al procesar mensaje FC: ${e.message}", e)
        }
    }

    fun disconnect() {
        try {
            if (client?.isConnected == true) {
                client?.disconnect()
            }
            scope.cancel()
        } catch (e: Exception) {
            android.util.Log.e("MQTT_APP", "Error al desconectar: ${e.message}")
        }
    }
}
