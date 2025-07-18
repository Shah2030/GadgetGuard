pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // Correct for Kotlin DSL
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "GadgetGuard"
include(":app")
