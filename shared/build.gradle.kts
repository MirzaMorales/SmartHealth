import java.util.Properties

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    alias(libs.plugins.kotlin.serialization)
}

val localProps = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProps.load(localPropertiesFile.inputStream())
}

android {
    namespace = "mx.utng.mnml.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24 // Compatible with both app (24) and wear (30)
        buildConfigField("String", "NEON_API_KEY", "\"${localProps["NEON_API_KEY"] ?: ""}\"")
        buildConfigField("String", "NEON_HOST", "\"${localProps["NEON_HOST"] ?: ""}\"")
        buildConfigField("String", "NEON_USER", "\"${localProps["NEON_USER"] ?: ""}\"")
        buildConfigField("String", "NEON_PASSWORD", "\"${localProps["NEON_PASSWORD"] ?: ""}\"")
        buildConfigField("String", "NEON_DB", "\"${localProps["NEON_DB"] ?: "neondb"}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    
    // Room
    val roomVersion = "2.8.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Retrofit + OkHttp para llamadas a Neon HTTP API
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlinx Serialization para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

