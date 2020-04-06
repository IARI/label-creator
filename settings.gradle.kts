pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                //useModule("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:${requested.version}")
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
    repositories {
        jcenter()
        gradlePluginPortal()
        maven("https://kotlin.bintray.com/kotlinx")
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "LabelCreator"

