pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // This setting forces all repositories to be declared here (not in subprojects)
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        google()
        mavenCentral()
        
        // ============ ADD CUSTOM REPOSITORY FOR libbox ============
        maven {
            url = uri("https://maven.nekohasekai.io/repository/maven-public/")
        }
    }
}

rootProject.name = "Gtunnel-Plus"
include(":app")
// include any other modules you have (e.g., ":libxposed-api")