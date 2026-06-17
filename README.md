
# SmartHealth Monitor
 
Aplicación Android multiplataforma para monitoreo de salud personal. Desarrollada como proyecto integrador en UTNG — 9° Cuatrimestre 2026.
 
## Stack tecnológico
-	Kotlin + Jetpack Compose
-	Material Design 3
-	Wearable Data Layer API (Wear OS)
-	Android TV / Leanback + Media3
-	Jetpack Navigation + Room + StateFlow

# 1.4 LoginScreen Compose S4'

<img width="1160" height="962" alt="image" src="https://github.com/user-attachments/assets/3f295e22-ccb1-4669-93bf-4d698c65ec93" />

# 2.1 DashboardScreen: Cards de datos, LazyColumn e Historial navegable

<img width="399" height="848" alt="image" src="https://github.com/user-attachments/assets/032226df-248a-4293-b308-e5b26313cef9" /> <img width="376" height="834" alt="image" src="https://github.com/user-attachments/assets/c31b3c9c-76d5-47db-ae10-c39f05659ce8" />

*Video de Funcionalidad*
https://drive.google.com/file/d/1VYGtXJmMK7w89imJ_O8ATBJ1omZjoZua/view

## Pantallas implementadas
-	[x] LoginScreen — S4
-	[x] DashboardScreen — S5
-	[ ] Historial + wearable real — S6
-	[ ] Android TV — S10-S12
 

# 2.2 Wearable Data Layer API + GitHub como flujo de trabajo profesional

<img width="1919" height="1048" alt="image" src="https://github.com/user-attachments/assets/65cac5ea-25e3-41a1-a987-d0848577c5b1" />

*Video de Funcionalidad*
https://drive.google.com/file/d/1k38ShlZ7sOqRW5ZWI2rTizMcFo7BNtbS/view

## Archivos modificados/creados
-	[ ] data/SmartHealthRepository.kt — StateFlow centralizado
-	[ ] data/WearListenerService.kt — receptor de mensajes BLE
-	[ ] ui/viewmodel/DashboardViewModel.kt — puente repo → UI
-	[ ] ui/screens/DashboardScreen.kt — consume ViewModel- [ ] AndroidManifest.xml — registra WearListenerService
 
## Cómo probar 1. Ejecutar la app en emulador Pixel 6.
2.	Presionar 'Simular dato del wearable'.
3.	Verificar que los valores de FC y Pasos cambian en tiempo real.


# 2.3 Health Services API + Room DB: Datos reales del sensor + Historial persistente

<img width="601" height="786" alt="image" src="https://github.com/user-attachments/assets/9ce664b4-9ac1-431a-a32a-cd8893d99689" />

*Video de Funcionalidad*
https://drive.google.com/file/u/0/d/16zWDpobaHUmow-x6LzFRDfGXnf9QIfGU/view?usp=classroom_web

## ¿Qué hace este PR?
Integra Health Services API para lectura real del sensor FC del wearable.
Agrega Room DB para persistir el historial de lecturas. Conecta HistorialScreen con Room vía StateFlow reactivo.
 
## Archivos creados/modificados
-	[x] wear/.../HealthDataService.kt — PassiveMonitoringClient
-	[x] data/db/LecturaFC.kt — @Entity Room
-	[x] data/db/LecturaFCDao.kt — @Dao con Flow
-	[x] data/db/SmartHealthDB.kt — @Database singleton
-	[x] data/SmartHealthRepository.kt — actualizado con Room
-	[x] ui/viewmodel/DashboardViewModel.kt — historial desde Room
-	[x] ui/screens/HistorialScreen.kt — completo con estado vacío
-	[x] navigation/NavGraph.kt — HistorialScreen real
 
## Cómo probar 1. Emulador Wear OS → Extended Controls → Health Services → mover slider FC.
2.	Verificar que Dashboard muestra el valor del slider en tiempo real.
3.	Abrir Historial: lecturas deben aparecer en orden descendente.
4.	FC > 100 debe aparecer en rojo.
5.	Cerrar y reabrir la app: historial debe persistir.

# 2.4 AlertaScreen

<img width="601" height="789" alt="image" src="https://github.com/user-attachments/assets/4d846858-b3d3-4db1-90d9-cb11630ee92e" />

*Video de funcionalidad*
https://drive.google.com/file/u/0/d/17WXRIVNLOHMSAvFKayQQFp1Btgt4qZjb/view?usp=classroom_web

## ¿Qué hace este PR?
Completa la Unidad I de SmartHealth Monitor con:
-	AlertaScreen: AlertDialog MD3 + spinner de confirmación
-	Integración en DashboardScreen: FAB → Dialog → Snackbar
-	README profesional con capturas de las 4 pantallas
 
## Archivos creados/modificados
-	[x] ui/screens/AlertaScreen.kt
-	[x] ui/screens/DashboardScreen.kt (FAB + Dialog state + SnackbarHost)
-	[x] README.md (completo con capturas)
-	[x] screenshots/ (4 capturas)
 
## Flujo completo verificado 1. Login → Dashboard: datos reales del wearable o simulados.
2.	Dashboard → Historial: datos persistidos en Room.
3.	Dashboard → FAB → AlertDialog: FC actual visible, botón CONFIRMAR.
4.	Confirmar → Dialog cierra + Snackbar de confirmación.
5.	Cerrar y reabrir la app: historial persiste.

# 2.5 Compose for Wear OS: Interfaz nativa del reloj inteligente

<img width="792" height="1032" alt="Captura de pantalla 2026-06-16 232610" src="https://github.com/user-attachments/assets/97677cbe-1d33-4496-bded-423cfa5d80a5" />
<img width="1918" height="1012" alt="Captura de pantalla 2026-06-16 231153" src="https://github.com/user-attachments/assets/d6b1db15-8f57-419e-acb9-eda3e73fc23c" />


*Videos de funcionalidad*
https://drive.google.com/file/u/0/d/19MOh1hCPmD-NUJcqexHBoFNW6APgLDc5/view?usp=classroom_web
https://drive.google.com/file/d/1gCU5L1YutDQ9KtgYWfoDGr5wcdxigSzH/view

## ¿Qué hace este PR?
Implementa la interfaz nativa para Wear OS utilizando Jetpack Compose for Wear OS y Google Horologist. Integra la navegación gestual circular optimizada para relojes (deslizar para salir), lectura inmediata y en tiempo real del sensor de frecuencia cardíaca (FC) usando el `SensorManager` del dispositivo (emulador) para sincronizar los cambios de pulso al instante con la app del celular, y una pantalla de confirmación interactiva para enviar alertas médicas.

## Archivos creados/modificados
- [x] wear/src/main/AndroidManifest.xml — Declaración de permisos de salud, sensores y componentes de la aplicación.
- [x] wear/build.gradle.kts — Dependencias de Compose for Wear OS, Horologist, iconos y módulo shared.
- [x] wear/.../presentation/WearMainActivity.kt — Petición de permisos dinámicos en ejecución e inicialización del SensorManager para actualizaciones inmediatas.
- [x] wear/.../presentation/WearDashboardScreen.kt — Pantalla principal del reloj con Scaffold circular, indicador de posición y lista deslizable.
- [x] wear/.../presentation/WearDashboardViewModel.kt — Conector que expone la frecuencia cardíaca en tiempo real desde el repositorio.
- [x] wear/.../presentation/WearAlertaScreen.kt — Interfaz de confirmación de alertas con botones rápidos de Confirmar/Cancelar.
- [x] wear/.../presentation/WearNavGraph.kt — Controlador de navegación mediante `SwipeDismissableNavHost`.
- [x] wear/.../presentation/components/WearFCCard.kt — Tarjeta reutilizable que dibuja el pulso (BPM) en tiempo real con colores dinámicos (color de error si está fuera de rango).
- [x] wear/.../presentation/theme/WearTheme.kt — Tema base circular adaptado a Material Design en relojes.
- [x] settings.gradle.kts — Registro e inclusión del módulo de código compartido `:shared`.

## Flujo completo verificado
1. **Petición de Permisos:** Al abrir por primera vez la aplicación en el Wear OS, solicita los permisos de sensores (`BODY_SENSORS`), reconocimiento de actividad y salud.
2. **Dashboard en tiempo real:** Tras otorgar los permisos, carga el Dashboard mostrando la hora actual, la tarjeta de FC y el botón de Alerta.
3. **Simulador de Pulso:** Modificar la frecuencia cardíaca desde los *Virtual Sensors* (pestaña Heart Rate) del emulador actualiza la tarjeta de pulso instantáneamente en la pantalla del reloj.
4. **Alerta y Navegación Circular:** Presionar "Alerta" abre la pantalla de confirmación. Deslizar hacia la derecha (swipe right) regresa suavemente al Dashboard gracias a `SwipeDismissableNavHost`.
5. **Sincronización Multidispositivo:** Cada cambio de pulso leído en tiempo real por el reloj es enviado automáticamente y se actualiza de inmediato en la aplicación móvil del celular.

# 2.6 Wear OS Avanzado: Rotary Input · SwipeToDismiss · WatchFace
<img width="547" height="577" alt="Captura de pantalla 2026-06-17 122200" src="https://github.com/user-attachments/assets/177614b4-ac04-4ad0-81d9-87e70b6cc915" />
<img width="576" height="652" alt="Captura de pantalla 2026-06-17 134137" src="https://github.com/user-attachments/assets/c1488986-37da-4d9a-92f9-59fa569c95e7" />

## ¿Qué hace este PR?
Implementa una esfera de reloj inteligente (Watch Face) interactiva nativa utilizando la API Jetpack WatchFace con CanvasRenderer, e integra una pantalla de historial de frecuencia cardíaca (WearHistorialScreen) que soporta entrada física rotatoria (corona/bisel del reloj) a través de .rotaryScrollable de Wear Compose y Google Horologist. Consume las lecturas de frecuencia cardíaca del sensor del dispositivo en tiempo real directamente en la esfera del reloj y consulta la base de datos local compartida (Room DB a través del repositorio :shared) para listar y resaltar visualmente las lecturas históricas según su rango.

## Archivos creados/modificados
- [x] wear/build.gradle.kts — Dependencias de Jetpack WatchFace API (watchface, watchface-style) y plugin de KSP.
- [x] wear/src/main/AndroidManifest.xml — Registro del servicio de la esfera de reloj (SmartHealthWatchFaceService) con los permisos correspondientes.
- [x] wear/src/main/res/xml/watch_face.xml — Recurso XML de la esfera de reloj y definición del thumbnail de vista previa.
- [x] wear/src/main/res/drawable/preview_digital.png — Miniatura de vista previa de la esfera del reloj digital en los ajustes.
- [x] wear/.../watchface/SmartHealthWatchFaceService.kt — Servicio para inicializar la esfera del reloj digital e instanciar el renderer.
- [x] wear/.../watchface/SmartHealthRenderer.kt — Motor CanvasRenderer que escucha al sensor de ritmo cardíaco en tiempo real y dibuja la hora y el pulso sobre fondo optimizado.
- [x] wear/.../presentation/WearDashboardScreen.kt — Inclusión del botón (Chip) para acceder a la nueva pantalla de Historial.
- [x] wear/.../presentation/WearDashboardViewModel.kt — Exposición del flujo de datos de historial obtenidos del repositorio compartido.
- [x] wear/.../presentation/WearNavGraph.kt — Controlador de navegación con soporte para la pantalla de historial en el host circular.
- [x] wear/.../presentation/WearHistorialScreen.kt — Pantalla de historial con lista deslizable y soporte para desplazamiento por corona física/rotatoria.
- [x] wear/.../presentation/components/WearFilaHistorial.kt — Fila/tarjeta del historial con color de pulso dinámico (color de error si está fuera de rango).

## Flujo completo verificado
1. **Configuración de Esfera de Reloj (Watch Face):** Al cambiar la esfera del reloj en el emulador o reloj inteligente Wear OS, aparece "SmartHealth Watch Face" con su respectiva vista previa (preview_digital.png). Al seleccionarla, comienza a mostrar de forma activa la hora, los segundos y las pulsaciones actuales.
2. **Monitoreo en Esfera de Reloj en Segundo Plano:** El renderizador de la esfera del reloj registra dinámicamente un listener del sensor de ritmo cardíaco. Cada cambio del pulso desde los sensores virtuales actualiza instantáneamente el Canvas y persiste los datos en el repositorio compartido sin necesidad de que la aplicación principal esté abierta.
3. **Navegación al Historial:** En la aplicación principal, el Dashboard ahora cuenta con un botón "Historial". Al presionarlo, navega a la pantalla WearHistorialScreen cargando la lista de registros históricos en orden cronológico.
4. **Desplazamiento Rotatorio (Rotary Input):** El usuario puede navegar de manera fluida a lo largo del historial de lecturas usando la corona física o el bisel táctil del dispositivo gracias a la integración del modificador .rotaryScrollable.
5. **Resaltado Visual de Alertas:** Las lecturas almacenadas en la base de datos se clasifican automáticamente en base a si el pulso es normal o anómalo. El historial visualiza de inmediato la lectura anómala pintando el pulso en color rojo (Error Theme) y las lecturas normales en color verde/azul del tema principal.
6. **Navegación Circular:** Deslizar de izquierda a derecha (Swipe to dismiss) en la pantalla de historial regresa suavemente al Dashboard gracias al comportamiento del SwipeDismissableNavHost.

## Autor
Mirza Morales — UTNG — natzllyunigmail.com



