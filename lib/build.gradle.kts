plugins {
    kotlin("jvm") version "2.1.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
