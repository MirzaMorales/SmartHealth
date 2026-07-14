package mx.utng.smarthealthmonitor.tv.mqtt

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import mx.utng.mnml.smarthealthmonitor.mqtt.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttTvSubscriber(
    private val context: Context,
    private val tvFlow: MutableStateFlow<TvMessage?> // actualiza el ViewModel
) {
    private var client: MqttAsyncClient? = null

    fun connect() {
        try {
            val uniqueClientId = "${MqttConfig.CLIENT_TV}-${java.util.UUID.randomUUID().toString().take(8)}"
            client = MqttAsyncClient(
                MqttConfig.BROKER_URL,
                uniqueClientId,
                MemoryPersistence()
            )

            client?.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String, msg: MqttMessage) {
                    if (topic == MqttConfig.TOPIC_TV) {
                        try {
                            val payloadString = String(msg.payload)
                            android.util.Log.d("MQTT_TV", "Mensaje recibido en TV: $payloadString")
                            val tvMsg = Json.decodeFromString<TvMessage>(payloadString)
                            tvFlow.value = tvMsg
                            android.util.Log.d("MQTT_TV", "   Recibido: ${tvMsg.bpm} bpm")
                        } catch (e: Exception) {
                            android.util.Log.e("MQTT_TV", "Error al procesar mensaje TV: ${e.message}")
                        }
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    android.util.Log.w("MQTT_TV", "Conexión perdida en TV: ${cause?.message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            val options = MqttConnectOptions().apply {
                userName = MqttConfig.USERNAME
                password = MqttConfig.PASSWORD.toCharArray()
                isCleanSession = true
                socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
            }

            client?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(token: IMqttToken?) {
                    try {
                        client?.subscribe(MqttConfig.TOPIC_TV, MqttConfig.QOS)
                        android.util.Log.d("MQTT_TV", "✅ TV suscrita a ${MqttConfig.TOPIC_TV}")
                    } catch (e: Exception) {
                        android.util.Log.e("MQTT_TV", "Error al suscribirse al conectar: ${e.message}")
                    }
                }

                override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                    android.util.Log.e("MQTT_TV", "❌ Error al conectar TV: ${ex?.message}")
                }
            })

        } catch (e: Exception) {
            android.util.Log.e("MQTT_TV", "Error al inicializar MqttTvSubscriber: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            if (client?.isConnected == true) {
                client?.disconnect()
            }
        } catch (e: Exception) {
            android.util.Log.e("MQTT_TV", "Error al desconectar: ${e.message}")
        }
    }
}
