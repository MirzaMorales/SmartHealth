package mx.utng.mnml.smarthealthmonitor.mqtt

object MqttConfig {
    // Datos del clúster de HiveMQ Cloud provistos por el usuario
    const val BROKER_URL = "ssl://842f58538fa045f4921e75221c973cd5.s1.eu.hivemq.cloud:8883"
    const val USERNAME = "usuario-smarthealth2"
    const val PASSWORD = "Mirza123!"

    // Topics del proyecto (UTNG)
    const val TOPIC_FC = "utng/smarthealthmonitor/fc"
    const val TOPIC_TV = "utng/smarthealthmonitor/tv"
    const val TOPIC_ALERT = "utng/smarthealthmonitor/alerta"

    // QoS (Quality of Service): 0 = At most once (Evita la necesidad de PUBACK y previene desconexiones por buffering en Wear OS)
    const val QOS = 0

    // Client IDs únicos por dispositivo para evitar conflictos de reconexión
    const val CLIENT_WEAR = "smarthealthmonitor-wear"
    const val CLIENT_APP = "smarthealthmonitor-app"
    const val CLIENT_TV = "smarthealthmonitor-tv"
}
