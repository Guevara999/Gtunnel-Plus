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
        // The custom Maven repository has been removed – we now use a local libbox.aar
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")
// If you have other modules, include them here (e.g., ":libxposed-api")
// include(":libxposed-api")