plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("app.cash.sqldelight")
}

group = "com.equationl"
version = "1.2.4"

kotlin {
    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.2.4"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        // extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    jvm("desktop") {
        jvmToolchain(18)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                // implementation("app.cash.sqldelight:runtime:2.0.0")
                implementation("com.ionspin.kotlin:bignum:0.3.9")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
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
                api("androidx.core:core-ktx:1.10.1")
                api("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
                api("com.blankj:utilcode:1.30.7")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
                api("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
                api("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
                api("androidx.lifecycle:lifecycle-service:2.6.1")

                implementation("app.cash.sqldelight:android-driver:2.0.0")
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
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            }
        }
        val desktopTest by getting

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.0.0")
            }
        }
    }
}

android {
    namespace = "com.equationl.common"

    compileSdk = 34
    // sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
}

sqldelight {
    databases {
        create("HistoryDatabase") {
            packageName.set("com.equationl.common.database")
        }
    }
}