package mx.utng.mnml.wear.mqtt

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mx.utng.mnml.smarthealthmonitor.mqtt.MqttConfig
import mx.utng.mnml.smarthealthmonitor.mqtt.FcMessage
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object MqttWearPublisher {
    private var client: MqttAsyncClient? = null
    private var isConnecting = false
    private var wasConnectedAtLeastOnce = false

    @Synchronized
    fun connect() {
        if (client?.isConnected == true) {
            android.util.Log.d("MQTT_WEAR", "Ya está conectado a HiveMQ Cloud")
            return
        }
        if (isConnecting) {
            android.util.Log.d("MQTT_WEAR", "Conexión en progreso, ignorando solicitud duplicada")
            return
        }
        if (client != null && wasConnectedAtLeastOnce) {
            android.util.Log.d("MQTT_WEAR", "El cliente está desconectado pero la reconexión automática está activa")
            return
        }
        
        isConnecting = true
        try {
            if (client == null) {
                val uniqueClientId = "${MqttConfig.CLIENT_WEAR}-${java.util.UUID.randomUUID().toString().take(8)}"
                client = MqttAsyncClient(
                    MqttConfig.BROKER_URL,
                    uniqueClientId,
                    MemoryPersistence()
                )

                client?.setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        synchronized(this@MqttWearPublisher) {
                            isConnecting = false
                            wasConnectedAtLeastOnce = true
                        }
                        android.util.Log.d("MQTT_WEAR", "✅ Conexión completada (reconnect=$reconnect) a HiveMQ Cloud")
                    }

                    override fun connectionLost(cause: Throwable?) {
                        android.util.Log.e("MQTT_WEAR", "⚠️ Conexión perdida: ${cause?.message}", cause)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {}

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
                })
            }

            val options = MqttConnectOptions().apply {
                userName = MqttConfig.USERNAME
                password = MqttConfig.PASSWORD.toCharArray()
                isCleanSession = true
                connectionTimeout = 30
                keepAliveInterval = 60
                isAutomaticReconnect = true
                socketFactory = javax.net.ssl.SSLSocketFactory.getDefault()
            }

            client?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    synchronized(this@MqttWearPublisher) {
                        isConnecting = false
                        wasConnectedAtLeastOnce = true
                    }
                    android.util.Log.d("MQTT_WEAR", "✅ Conexión inicial exitosa a HiveMQ Cloud")
                }
                override fun onFailure(token: IMqttToken?, ex: Throwable?) {
                    synchronized(this@MqttWearPublisher) {
                        isConnecting = false
                    }
                    android.util.Log.e("MQTT_WEAR", "❌ Error en conexión inicial: ${ex?.message}", ex)
                }
            })
        } catch (e: Exception) {
            isConnecting = false
            android.util.Log.e("MQTT_WEAR", "Error al conectar: ${e.message}", e)
        }
    }

    /** Publicar FC al topic MQTT */
    @Synchronized
    fun publishFC(bpm: Int, estado: String) {
        val activeClient = client
        if (activeClient == null || !activeClient.isConnected) {
            android.util.Log.w("MQTT_WEAR", "No se puede publicar: Cliente desconectado. Intentando conectar...")
            connect()
            return
        }
        try {
            val message = FcMessage(bpm = bpm, estado = estado)
            val payload = Json.encodeToString(message).toByteArray()
            val mqttMessage = MqttMessage(payload).apply {
                qos = MqttConfig.QOS
                isRetained = false // Deshabilitar retained para evitar desconexiones por políticas del broker
            }
            activeClient.publish(MqttConfig.TOPIC_FC, mqttMessage)
            android.util.Log.d("MQTT_WEAR", "   Publicado: ${bpm} bpm → ${MqttConfig.TOPIC_FC}")
        } catch (e: Exception) {
            android.util.Log.e("MQTT_WEAR", "Error al publicar FC: ${e.message}", e)
        }
    }

    @Synchronized
    fun disconnect() {
        try {
            if (client?.isConnected == true) {
                client?.disconnect()
            }
            wasConnectedAtLeastOnce = false
        } catch (e: Exception) {
            android.util.Log.e("MQTT_WEAR", "Error al desconectar: ${e.message}", e)
        }
    }
}
