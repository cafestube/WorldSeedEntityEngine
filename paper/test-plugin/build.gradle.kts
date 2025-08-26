import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("xyz.jpenilla.run-paper") version "3.0.0-beta.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0" // Generates plugin.yml based on the Gradle config
    id("com.gradleup.shadow") version "9.0.2"
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("commons-io:commons-io:2.20.0")
    implementation("org.zeroturnaround:zt-zip:1.17")
    implementation(project(":paper"))
    implementation(project(":resource-pack"))

    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

bukkitPluginYaml {
    main = "net.worldseed.paper.test.TestPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("WSSE Contributors")
    apiVersion = "1.21.8"
}


tasks.withType(xyz.jpenilla.runpaper.task.RunServer::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    minecraftVersion("1.21.8")

    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    jvmArgs("-Dcom.mojang.eula.agree=true")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
