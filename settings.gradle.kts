pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "WorldSeedEntityEngine"


include("resource-pack")
include("minestom")
include("common")
include("paper")
include("paper:test-plugin")