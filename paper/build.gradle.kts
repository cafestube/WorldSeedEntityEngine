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
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0-M2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0-M2")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    implementation(project(":common"))
    implementation("dev.hollowcube:mql:1.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
