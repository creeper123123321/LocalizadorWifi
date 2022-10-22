import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "br.nom.yt.re.ifciencia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.1.2")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.1.2")
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core-jvm:2.1.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.1.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.1.2")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.1.2")
    implementation("io.ktor:ktor-network-tls-certificates:2.1.2")
    implementation("io.ktor:ktor-server-config-yaml:2.1.2")
    implementation("io.ktor:ktor-serialization-gson:2.1.2")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("com.google.code.gson:gson:2.9.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("br.nom.yt.re.ifciencia.localizadorwifi.MainKt")
}