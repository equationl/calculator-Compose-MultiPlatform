plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "com.equationl"
version = "1.2.4"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.7.2")
}

android {
    namespace = "com.equationl.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.equationl.calculator_compose"
        minSdk = 24
        targetSdk = 34
        versionCode = 9
        versionName = "1.2.4"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    dexOptions {
        javaMaxHeapSize = "4G"
    }
}