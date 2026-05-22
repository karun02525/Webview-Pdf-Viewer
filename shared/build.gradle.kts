import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

group = "com.prikaro"
version = "1.0.0"

kotlin {
    android {
        namespace = "com.prikaro.pdfviewer"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
        androidResources { enable = true }
        withHostTest { isIncludeAndroidResources = true }
    }

    iosArm64()
    iosSimulatorArm64()

    targets
        .withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
        .configureEach {
            binaries.framework {
                baseName = "PdfViewerKMP"
                isStatic = true
            }
        }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.material.icons.extended)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

mavenPublishing {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = "com.prikaro",
        artifactId = "webview-pdf-viewer",
        version = "1.0.0",
    )

    pom {
        name.set("Webview PDF Viewer")
        description.set(
            "A Kotlin Multiplatform PDF Viewer library with Compose UI for Android and iOS"
        )
        url.set("https://github.com/karun02525/Webview-Pdf-Viewer")
        inceptionYear.set("2026")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("karun02525")
                name.set("Karun")
                email.set("karunkumar02525@gmail.com")
                url.set("https://github.com/karun02525")
            }
        }

        scm {
            url.set("https://github.com/karun02525/webview-pdf-viewer")
            connection.set(
                "scm:git:git://github.com/karun02525/webview-pdf-viewer.git"
            )
            developerConnection.set(
                "scm:git:ssh://git@github.com/karun02525/webview-pdf-viewer.git"
            )
        }
    }
}

tasks.withType<Sign>().configureEach {
    val isLocal = gradle.startParameter.taskNames  // config-time safe
        .any { it.contains("ToMavenLocal") }
    onlyIf { !isLocal }
}