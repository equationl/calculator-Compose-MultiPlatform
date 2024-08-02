import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.equationl"
version = "1.2.4"


kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "calculator_compose" // 使用中文名称在某些版本会导致无法运行，所以改成英文名
            packageVersion = "1.2.4"

            macOS {
                iconFile.set(project.file("icon.icns"))
            }
        }


        buildTypes.release.proguard {
            // fixme test obfuscate.set(true)
            obfuscate.set(false)
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}
