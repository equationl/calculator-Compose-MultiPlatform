plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("app.cash.sqldelight")
}

group = "com.equationl"
version = "1.0"

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(11)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                implementation("app.cash.sqldelight:runtime:2.0.0-alpha05")

                // 以下依赖是安卓平台的特有依赖，在打包桌面端时请注释掉，否则会打包失败
                api("com.blankj:utilcode:1.30.7")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
                api("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
                api("androidx.lifecycle:lifecycle-service:2.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")
                api("com.google.accompanist:accompanist-systemuicontroller:0.25.1")
                implementation("app.cash.sqldelight:android-driver:2.0.0-alpha05")
            }
        }
        /*val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }*/
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(33)
    // sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("HistoryDatabase") {
            packageName.set("com.equationl.common.database")
        }
    }
}