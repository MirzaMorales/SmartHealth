
# SmartHealth Monitor
 
Aplicación Android multiplataforma para monitoreo de salud personal. Desarrollada como proyecto integrador en UTNG — 9° Cuatrimestre 2026.
 
## Stack tecnológico
-	Kotlin + Jetpack Compose
-	Material Design 3
-	Wearable Data Layer API (Wear OS)
-	Android TV / Leanback + Media3
-	Jetpack Navigation + Room + StateFlow
 
## Pantallas implementadas
-	[x] LoginScreen — S4
-	[x] DashboardScreen — S5
-	[ ] Historial + wearable real — S6
-	[ ] Android TV — S10-S12
 

## 1.4 LoginScreen Compose S4'

<img width="1160" height="962" alt="image" src="https://github.com/user-attachments/assets/3f295e22-ccb1-4669-93bf-4d698c65ec93" />

## 2.1 DashboardScreen: Cards de datos, LazyColumn e Historial navegable

<img width="399" height="848" alt="image" src="https://github.com/user-attachments/assets/032226df-248a-4293-b308-e5b26313cef9" /> <img width="376" height="834" alt="image" src="https://github.com/user-attachments/assets/c31b3c9c-76d5-47db-ae10-c39f05659ce8" />

*Video de Funcionalidad*
https://drive.google.com/file/d/1VYGtXJmMK7w89imJ_O8ATBJ1omZjoZua/view

## 2.2 Wearable Data Layer API + GitHub como flujo de trabajo profesional

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



## Autor
Mirza Morales — UTNG — natzllyunigmail.com



