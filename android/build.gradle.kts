plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "com.equationl"
version = "1.2"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.7.2")
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.equationl.calculator_compose"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    dexOptions {
        javaMaxHeapSize = "4G"
    }
}