plugins {
    id("java")
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "net.cafestube.multipart"
        artifactId = "WorldSeedEntityEngine"
        version = "11.3.3"

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

    compileOnly("net.minestom:minestom:2025.08.12-1.21.8")
    testImplementation("net.minestom:minestom:2025.08.12-1.21.8")
    testImplementation(project(":resource-pack"))

    testImplementation("commons-io:commons-io:2.20.0")
    testImplementation("org.zeroturnaround:zt-zip:1.17")

    implementation("dev.hollowcube:molang:1.0.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
