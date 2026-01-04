plugins {
    id("java-library")
    `maven-publish`
    signing
}

java {

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

version = property("version") as String

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "net.cafestube.multipart"
        artifactId = "WorldSeedEntityEngineMinestom"

        from(components["java"])
    }

    repositories {
        maven {
            name = "cafestubeRepository"
            credentials(PasswordCredentials::class)
            url = uri("https://repo.cafestube.net/repository/maven-public-snapshots/")
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0-M2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0-M2")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")

    compileOnly("net.minestom:minestom:2025.12.19-1.21.10")
    testImplementation("net.minestom:minestom:2025.08.12-1.21.8")
    testImplementation(project(":resource-pack"))
    api(project(":common"))

    testImplementation("commons-io:commons-io:2.20.0")
    testImplementation("org.zeroturnaround:zt-zip:1.17")

    implementation("dev.hollowcube:molang:1.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
