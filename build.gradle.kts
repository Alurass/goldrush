plugins {
    id("java")
}

group = "dev.aluras"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:${project.property("minestomVersion")}")
}