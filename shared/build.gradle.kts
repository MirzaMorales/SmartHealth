plugins {
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

android {
    namespace = "mx.utng.mnml.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 24 // Compatible with both app (24) and wear (30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
}
