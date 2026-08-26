pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Use PREFER_SETTINGS so project repos can supplement
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // JitPack – for libsu, compose-markdown, etc.
        maven { url = uri("https://jitpack.io") }
        // Nekohasekai – for libghostty (if kept) and other SagerNet artifacts
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-public/") }
        // Snapshots for SNAPSHOT versions
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-snapshots/") }
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")
// If you have other modules, include them here
// include(":libxposed-api")