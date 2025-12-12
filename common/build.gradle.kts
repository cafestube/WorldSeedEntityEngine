plugins {
    id("java")
    kotlin("jvm")
    id("maven-publish")
}

group = "net.cafestube"
version = property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.kyori:adventure-api:4.24.0")

    implementation("com.google.code.gson:gson:2.13.1")

    implementation("dev.hollowcube:molang:1.0.2")
    implementation(kotlin("stdlib-jdk8"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "net.cafestube.multipart"
        artifactId = "WorldSeedEntityEngineCommon"

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

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}