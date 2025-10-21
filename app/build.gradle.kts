plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // necesario para Room (compiler)
}

android {
    namespace = "com.example.odontoapp"
    compileSdk {
        // mantengo tu DSL; si te da error, usa: compileSdk = 36
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.odontoapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
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

    // ⚠️ Sube a Java/Kotlin 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    // Si usas composeOptions con versión explícita, puedes mantenerla:
    // composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
}

dependencies {
    // --- tus deps existentes ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.compose.material:material-icons-extended")

    // --- añadidos para este proyecto ---

    // Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // ViewModel para Compose (state hoisting limpio)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Room (persistencia local)
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // WorkManager (solo si luego programas recordatorios; para notificación inmediata no es obligatorio)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Coil (mostrar imagen de la galería en el formulario de Paciente)
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")

// ViewModel + Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

// Room (persistencia local)
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

// Coil (mostrar imagen de galería)
    implementation("io.coil-kt:coil-compose:2.6.0")

// Íconos (si usas Icons.Filled.Error en ValidatedField)
    implementation("androidx.compose.material:material-icons-extended")

// Foundation (para FlowRow en BookingScreen)
    implementation("androidx.compose.foundation:foundation")

}
