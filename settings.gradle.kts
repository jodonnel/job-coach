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

        // Meta Wearables DAT SDK
        maven {
            url = uri("https://maven.pkg.github.com/facebook/meta-wearables-dat-android")
            credentials {
                username = ""
                password = providers.gradleProperty("dat.github.token")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                    .get()
            }
        }
    }
}

rootProject.name = "job-coach"
include(":app", ":core", ":wearable", ":stt")
