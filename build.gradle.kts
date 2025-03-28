plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "colosseum.gradle"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("ColosseumGradle") {
            id = "colosseum.gradle"
            implementationClass = "colosseum.gradle.ColosseumGradlePlugin"
        }
    }
}

publishing {
    System.getenv("COLOSSEUM_MAVEN_URL").let { mavenUrl ->
        if (mavenUrl == null) {
            return@let
        }
        println("Configuring publishing")
        repositories {
            maven {
                name = "ColosseumMaven"
                url = uri(mavenUrl)
                credentials {
                    username = System.getenv("COLOSSEUM_MAVEN_USERNAME")
                    password = System.getenv("COLOSSEUM_MAVEN_PASSWORD")
                }
            }
        }
    }
}