plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

android {
    namespace = "mx.utng.smarthealthmonitor.tv"
    compileSdk = 35

    defaultConfig {
        applicationId = "mx.utng.smarthealthmonitor.tv"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Leanback Library — el estándar de Android TV
    implementation("androidx.leanback:leanback:1.2.0")
    
    // Glide para cargar imágenes en las cards
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // Compatibilidad y componentes esenciales de vistas (AppCompat para Leanback)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    
    // Compartir Room + Repository con módulo shared
    implementation(project(":shared"))
    implementation("androidx.room:room-runtime:2.8.0")
    
    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // Compose Runtime necesario para satisfacer el plugin de Kotlin del proyecto
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.runtime:runtime")
}

