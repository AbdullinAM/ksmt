import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URI

plugins {
    id("org.ksmt.ksmt-base")
}

dependencies {
    testImplementation(project(":ksmt-z3"))
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.8.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += listOf("-Xjvm-default=all")
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
