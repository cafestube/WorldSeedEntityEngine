plugins {
    id("java-library")
    `maven-publish`
    signing

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

version = property("version") as String

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "net.cafestube.multipart"
        artifactId = "WorldSeedEntityEnginePaper"

        from(components["java"])
    }

    repositories {
        maven {
            name = "cafestubeRepository"
            credentials(PasswordCredentials::class)
            url = if(version.toString().endsWith("SNAPSHOT")) {
                uri("https://repo.cafestube.net/repository/maven-snapshots/")
            } else {
                uri("https://repo.cafestube.net/repository/maven-releases/")
            }
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")

    api(project(":common"))
    implementation("dev.hollowcube:molang:1.0.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
