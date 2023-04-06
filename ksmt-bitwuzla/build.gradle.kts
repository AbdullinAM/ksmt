import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URI

plugins {
    id("org.ksmt.ksmt-base")
}

repositories {
    mavenCentral()
    flatDir { dirs("dist") }
}

val bitwuzlaNativeX64 by configurations.creating
val bitwuzlaNativeArm by configurations.creating

dependencies {
    implementation(project(":ksmt-core"))

    bitwuzlaNativeX64("bitwuzla", "bitwuzla-linux64", "1.0", ext = "zip")
    bitwuzlaNativeX64("bitwuzla", "bitwuzla-win64", "1.0", ext = "zip")
    bitwuzlaNativeArm("bitwuzla", "bitwuzla-osx-arm64", "1.0", ext = "zip")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<ProcessResources> {
    sequenceOf("x64" to bitwuzlaNativeX64, "arm" to bitwuzlaNativeArm).forEach { (arch, config) ->
        config.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
            from(zipTree(artifact.file)) {
                into("lib/$arch")
            }
        }
    }
}

val deployUsername = stringOrEnvProperty("DEPLOY_USERNAME")
val deployPassword = stringOrEnvProperty("DEPLOY_PASSWORD")

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["kotlinSourcesJar"])
        }
    }
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
