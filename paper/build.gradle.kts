plugins {
    id("java-library")
    `maven-publish`
    signing

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
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
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
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
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0-M2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0-M2")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")

    api(project(":common"))
    implementation("dev.hollowcube:mql:1.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
