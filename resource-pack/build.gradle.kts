plugins {
    id("java")
    id("maven-publish")
}

group = "net.cafestube.multipart"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    implementation("javax.json:javax.json-api:1.1.4")
    implementation("org.glassfish:javax.json:1.1.4")
}

tasks.test {
    useJUnitPlatform()
}


publishing {
    publications.create<MavenPublication>("maven") {
        groupId = "net.cafestube.multipart"
        artifactId = "EntityEngineGenerator"

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
