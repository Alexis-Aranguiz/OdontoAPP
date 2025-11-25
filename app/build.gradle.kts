
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // necesario para Room
}

android {
    namespace = "com.example.odontoapp"

    compileSdk {
        // usa el helper propio de tu proyecto
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.odontoapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

        // 👇 Esto permite usar LocalDate, LocalTime, etc. en minSdk 24
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // -----------------------
    // COMPOSE + ANDROID CORE
    // -----------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // -----------------------
    // NAVIGATION COMPOSE
    // -----------------------
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // -----------------------
    // RETROFIT + MOSHI + LOGGING (APIs externas)
    // -----------------------
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // -----------------------
    // COIL (imágenes / foto de perfil)
    // -----------------------
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil:2.4.0")

    // -----------------------
    // ROOM (Base de datos local)
    // -----------------------
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // -----------------------
    // DESUGARING (java.time en minSdk 24)
    // -----------------------
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // -----------------------
    // TESTING EXTRA (MockK, Coroutines)
    // -----------------------
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
