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
        // Required for:
        // - com.github.jeziellago:compose-markdown
        // - com.github.topjohnwu.libsu:core and :service
        maven { url = uri("https://jitpack.io") }

        // Required for:
        // - io.github.sagernet:libghostty-android
        // - io.github.sagernet:libghostty-android-extras
        // - io.github.sagernet:libghostty-android-compose
        // (libbox is now provided locally, so we keep this repo only for these artifacts)
        maven { url = uri("https://maven.nekohasekai.io/repository/maven-public/") }
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")
// If you have other modules, include them here
// include(":libxposed-api")