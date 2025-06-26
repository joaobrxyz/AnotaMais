import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "com.example.anotamais"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.anotamais"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY") ?: ""}\""
        )

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
    }
}

dependencies {
    // Firebase BOM
    implementation(platform(libs.firebase.bom))

    // Firebase (para Java, sem -ktx)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-firestore") // versão via BOM
    implementation(libs.firebase.analytics)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Outros
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.generativeai)
    implementation(libs.circleimageview)
    implementation(libs.okhttp)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.guava)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
