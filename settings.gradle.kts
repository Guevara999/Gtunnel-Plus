pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // JitPack for libsu and compose-markdown
        maven { url = uri("https://jitpack.io") }
        // Nekohasekai repository for libghostty (if kept) and other SagerNet artifacts
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-public/") }
        // Snapshots repo for SNAPSHOT versions
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-snapshots/") }
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")