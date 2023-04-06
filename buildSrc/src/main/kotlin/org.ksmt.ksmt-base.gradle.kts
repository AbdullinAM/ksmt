import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("de.undercouch.download")
    `java-library`
    `maven-publish`
}

group = "org.ksmt"
version = "0.4.6-kex.0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    // Primitive collections
    implementation("it.unimi.dsi:fastutil-core:8.5.11") // 6.1MB

    testImplementation(kotlin("test"))
}

detekt {
    buildUponDefaultConfig = true
    config = files(rootDir.resolve("detekt.yml"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

tasks.getByName<KotlinCompile>("compileKotlin") {
    kotlinOptions.allWarningsAsErrors = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
}

val deployUsername = stringOrEnvProperty("DEPLOY_USERNAME")
val deployPassword = stringOrEnvProperty("DEPLOY_PASSWORD")

publishing {
    repositories {
        maven {
            url = URI("https://maven.pkg.github.com/vorpal-research/kotlin-maven")
            credentials {
                username = deployUsername
                password = deployPassword
            }

        }
    }
}
