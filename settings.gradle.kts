import java.util.Properties

val localProps = Properties()
val localPropsFile = file("local.properties")
if (localPropsFile.exists()) {
    localProps.load(localPropsFile.inputStream())
}

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
                password = localProps.getProperty("dat.github.token")
                    ?: System.getenv("GITHUB_TOKEN")
                    ?: ""
            }
        }
    }
}

rootProject.name = "job-coach"
include(":app", ":core", ":wearable", ":stt")
