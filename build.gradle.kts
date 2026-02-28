plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("java")
}

group = "com.binkibonk"
version = "0.1.3"
description = "Multipaper cross-server broadcast"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.clojars.org/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("com.github.puregero:multilib:1.2.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("net.kyori:adventure-text-minimessage:4.22.0")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
    relocate("kotlin", "com.binkibonk.civbroadcast.libs.kotlin")
    relocate("kotlinx", "com.binkibonk.civbroadcast.libs.kotlinx")
    relocate("com.github.puregero.multilib", "com.binkibonk.civbroadcast.libs.multilib")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
} 