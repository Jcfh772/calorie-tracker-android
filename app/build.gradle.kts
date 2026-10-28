import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

// The CalorieNinjas API key is read from local.properties, which git ignores.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.example.assignment2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.assignment2"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "CALORIE_NINJAS_API_KEY",
            "\"${localProperties.getProperty("CALORIE_NINJAS_API_KEY", "")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Firebase BoM
    implementation (platform(libs.firebase.bom))
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics)
    // Add the dependencies for the Firebase products you want to use
    implementation (libs.firebase.storage.ktx)
    implementation (libs.coil.compose)
    implementation (libs.accompanist.permissions)
    implementation (libs.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.common.ktx)
    kapt (libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.rxjava)
    implementation(libs.adapter.rxjava3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
