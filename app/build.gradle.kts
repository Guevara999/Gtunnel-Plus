import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.util.Base64
import java.util.Properties

plugins {
    id("com.android.application")
    // id("org.jetbrains.kotlin.android")  // ❌ Removed – no longer needed
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.triplet.play")
    alias(libs.plugins.spotless)
}

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.nekohasekai.io/repository/maven-public/") }
    maven { url = uri("https://maven.nekohasekai.io/repository/maven-snapshots/") }
}

fun getProps(propName: String): String {
    val propsInEnv = System.getenv("LOCAL_PROPERTIES")
    if (propsInEnv != null) {
        val props = Properties()
        props.load(ByteArrayInputStream(Base64.getDecoder().decode(propsInEnv)))
        val value = props.getProperty(propName)
        if (value != null) return value
    }
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        val props = Properties()
        props.load(FileInputStream(propsFile))
        val value = props.getProperty(propName)
        if (value != null) return value
    }
    return ""
}

fun getVersionProps(propName: String): String {
    val propsFile = rootProject.file("version.properties")
    if (propsFile.exists()) {
        val props = Properties()
        props.load(FileInputStream(propsFile))
        val value = props.getProperty(propName)
        if (value != null) return value
    }
    return ""
}

android {
    namespace = "io.nekohasekai.sfa"
    compileSdk = 37
    compileSdkMinor = 1

    ndkVersion = "27.3.13750724"  // Match workflow

    System.getenv("ANDROID_NDK_HOME")?.let { ndkPath = it }

    ksp {
        arg("room.incremental", "true")
        arg("room.schemaLocation", "${projectDir}/schemas")
    }

    defaultConfig {
        applicationId = "io.nekohasekai.sfa"
        minSdk = 24
        targetSdk = 37
        versionCode = getVersionProps("VERSION_CODE").toInt()
        versionName = getVersionProps("VERSION_NAME")
        base.archivesName.set("SFA-${versionName}")
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = getProps("KEYSTORE_PASS")
            keyAlias = getProps("ALIAS_NAME")
            keyPassword = getProps("ALIAS_PASS")
        }
    }

    buildTypes {
        debug {
            if (getProps("KEYSTORE_PASS").isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            vcsInfo.include = false
        }
    }

    dependenciesInfo {
        includeInApk = false
    }

    flavorDimensions += "vendor"
    productFlavors {
        create("play")
        create("other")
        create("otherLegacy") {
            minSdk = 21
        }
    }

    // Simple source sets – no exclusions (we'll delete folders in workflow)
    sourceSets {
        getByName("play") {
            java.setSrcDirs(listOf("src/minApi24/java"))
            aidl.setSrcDirs(listOf("src/minApi24/aidl"))
        }
        getByName("other") {
            java.setSrcDirs(listOf("src/minApi24/java", "src/github/java"))
            aidl.setSrcDirs(listOf("src/minApi24/aidl"))
        }
        getByName("otherLegacy") {
            java.setSrcDirs(listOf("src/minApi21/java", "src/github/java"))
            aidl.setSrcDirs(listOf("src/minApi24/aidl"))
        }
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildFeatures {
        viewBinding = true
        aidl = true
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    lint {
        fatal += "NewApi"
    }
}

// Use the new AndroidComponents extension to rename APKs
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val fileName = output.outputFileName
            output.outputFileName = fileName
                .replace("-release", "")
                .replace("-play", "-play")
                .replace("-otherLegacy", "-legacy-android-5")
                .replace("-other", "")
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(files("libs/libbox.aar"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")

    val lifecycleVersion = "2.11.0"
    val roomVersion = "2.8.4"
    val workVersion = "2.11.2"
    val cameraVersion = "1.6.1"
    val browserVersion = "1.10.0"
    val webkitVersion = "1.16.0"
    val coreVersion = "1.19.0"
    val materialVersion = "1.14.0"

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.8")
    implementation("com.google.zxing:core:3.5.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("com.android.tools.smali:smali-dexlib2:3.0.9") {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation("com.google.guava:guava:33.6.0-android")

    listOf("play", "other").forEach { flavor ->
        add("${flavor}Implementation", "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
        add("${flavor}Implementation", "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
        add("${flavor}Implementation", "androidx.lifecycle:lifecycle-process:$lifecycleVersion")
        add("${flavor}Implementation", "androidx.room:room-runtime:$roomVersion")
        add("${flavor}Implementation", "androidx.work:work-runtime-ktx:$workVersion")
        add("${flavor}Implementation", "androidx.camera:camera-view:$cameraVersion")
        add("${flavor}Implementation", "androidx.camera:camera-lifecycle:$cameraVersion")
        add("${flavor}Implementation", "androidx.camera:camera-camera2:$cameraVersion")
        add("${flavor}Implementation", "androidx.browser:browser:$browserVersion")
        add("${flavor}Implementation", "androidx.webkit:webkit:$webkitVersion")
        add("${flavor}Implementation", "androidx.core:core-ktx:$coreVersion")
        add("${flavor}Implementation", "com.google.android.material:material:$materialVersion")
        val kspFlavor = when (flavor) {
            "play" -> "kspPlay"
            "other" -> "kspOther"
            else -> "ksp${flavor.replaceFirstChar { it.uppercase() }}"
        }
        add(kspFlavor, "androidx.room:room-compiler:$roomVersion")
    }

    "otherLegacyImplementation"("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    "otherLegacyImplementation"("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    "otherLegacyImplementation"("androidx.lifecycle:lifecycle-process:2.9.4")
    "otherLegacyImplementation"("androidx.room:room-runtime:2.7.2")
    "otherLegacyImplementation"("androidx.work:work-runtime-ktx:2.10.5")
    "otherLegacyImplementation"("androidx.camera:camera-view:1.4.2")
    "otherLegacyImplementation"("androidx.camera:camera-lifecycle:1.4.2")
    "otherLegacyImplementation"("androidx.camera:camera-camera2:1.4.2")
    "otherLegacyImplementation"("androidx.browser:browser:1.9.0")
    "otherLegacyImplementation"("androidx.webkit:webkit:1.14.0")
    "otherLegacyImplementation"("androidx.core:core-ktx:1.17.0")
    "otherLegacyImplementation"("com.google.android.material:material:1.13.0")
    "kspOtherLegacy"("androidx.room:room-compiler:2.7.2")

    val soraVersion = "0.23.6"
    val treeSitterVersion = "4.3.2"
    listOf("play", "other").forEach { flavor ->
        add("${flavor}Implementation", "io.github.Rosemoe.sora-editor:editor:$soraVersion")
        add("${flavor}Implementation", "io.github.Rosemoe.sora-editor:language-treesitter:$soraVersion")
        add("${flavor}Implementation", "com.itsaky.androidide.treesitter:android-tree-sitter:$treeSitterVersion")
        add("${flavor}Implementation", "com.itsaky.androidide.treesitter:tree-sitter-json:$treeSitterVersion")
    }
    "otherLegacyImplementation"("com.blacksquircle.ui:editorkit:2.2.0")
    "otherLegacyImplementation"("com.blacksquircle.ui:language-json:2.2.0")

    "playImplementation"("com.google.android.play:app-update-ktx:2.1.0")
    "playImplementation"("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")

    val shizukuVersion = "13.1.5"
    listOf("play", "other").forEach { flavor ->
        add("${flavor}Implementation", "dev.rikka.shizuku:api:$shizukuVersion")
        add("${flavor}Implementation", "dev.rikka.shizuku:provider:$shizukuVersion")
    }

    val composeBom = platform("androidx.compose:compose-bom:2026.06.01")
    val activityVersion = "1.13.0"
    val lifecycleComposeVersion = "2.11.0"
    listOf("play", "other").forEach { flavor ->
        add("${flavor}Implementation", composeBom)
        add("${flavor}Implementation", "androidx.compose.material3:material3")
        add("${flavor}Implementation", "androidx.compose.material3.adaptive:adaptive")
        add("${flavor}Implementation", "androidx.compose.ui:ui")
        add("${flavor}Implementation", "androidx.compose.ui:ui-tooling-preview")
        add("${flavor}Implementation", "androidx.compose.material:material-icons-extended")
        add("${flavor}Implementation", "androidx.activity:activity-compose:$activityVersion")
        add("${flavor}Implementation", "androidx.navigation:navigation-compose:2.9.8")
        add("${flavor}Implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleComposeVersion")
        add("${flavor}Implementation", "androidx.compose.runtime:runtime-livedata")
    }
    val composeBomLegacy = platform("androidx.compose:compose-bom:2025.01.00")
    "otherLegacyImplementation"(composeBomLegacy)
    "otherLegacyImplementation"("androidx.compose.material3:material3")
    "otherLegacyImplementation"("androidx.compose.material3.adaptive:adaptive")
    "otherLegacyImplementation"("androidx.compose.ui:ui")
    "otherLegacyImplementation"("androidx.compose.ui:ui-tooling-preview")
    "otherLegacyImplementation"("androidx.compose.material:material-icons-extended")
    "otherLegacyImplementation"("androidx.activity:activity-compose:1.11.0")
    "otherLegacyImplementation"("androidx.navigation:navigation-compose:2.9.8")
    "otherLegacyImplementation"("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    "otherLegacyImplementation"("androidx.compose.runtime:runtime-livedata")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    implementation("sh.calvin.reorderable:reorderable:3.1.0")
}

val playCredentialsJSON = rootProject.file("service-account-credentials.json")
if (playCredentialsJSON.exists()) {
    play {
        serviceAccountCredentials.set(playCredentialsJSON)
        defaultToAppBundles.set(true)
        val version = getVersionProps("VERSION_NAME")
        track.set(
            if (version.contains("alpha") || version.contains("beta")) {
                "beta"
            } else {
                "production"
            }
        )
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(mapOf(
                "ktlint_standard_backing-property-naming" to "disabled",
                "ktlint_standard_blank-line-before-declaration" to "disabled",
                "ktlint_standard_blank-line-between-when-conditions" to "disabled",
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_max-line-length" to "disabled",
                "ktlint_standard_property-naming" to "disabled",
            ))
    }
    java {
        target("src/**/*.java")
        googleJavaFormat()
    }
}