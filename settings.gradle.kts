pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-public/") }
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-snapshots/") }
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")