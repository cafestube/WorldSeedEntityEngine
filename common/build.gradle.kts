plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.kyori:adventure-api:4.24.0")

    implementation("com.google.code.gson:gson:2.13.1")

    implementation("dev.hollowcube:mql:1.0.1")
}

tasks.test {
    useJUnitPlatform()
}