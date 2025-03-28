package colosseum.gradle.unittest

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ColosseumGradlePluginGroovyTest {
    @TempDir
    lateinit var projectDir: File
    private lateinit var buildGradle: File
    private lateinit var settingsGradle: File

    @BeforeEach
    fun setup() {
        buildGradle = projectDir.resolve("build.gradle")
        settingsGradle = projectDir.resolve("settings.gradle")
    }

    private fun testWithWithoutExtension(buildGradleContent: String, expectedOutput: String) {
        buildGradle.writeText(buildGradleContent)
        val res =
            GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments("dependencies", "--stacktrace")
                .withPluginClasspath()
                .forwardOutput()
                .build()
        assertTrue(res.output.contains(expectedOutput))
    }

    private fun testWithoutExtension() {
        testWithWithoutExtension(
            String.format(
                """
            plugins {
                id "java"
                id "colosseum.gradle"
            }
            repositories {
                mavenCentral()
                maven { url = "https://oss.sonatype.org/content/repositories/snapshots/" }
                maven { url = "https://oss.sonatype.org/content/repositories/central/" }
            }
            dependencies {
                compileOnly "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT"
            }
        """
            ).trimIndent(), "+--- net.md-5:bungeecord-chat:1.19-R0.1-SNAPSHOT (*)"
        )
    }

    @Test
    fun `Test extension (varargs)`() {
        testWithWithoutExtension(
            String.format(
                """
            plugins {
                id "java"
                id "colosseum.gradle"
            }
            repositories {
                colosseum {
                    colosseumContent([
                        maven { url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/" },
                        maven { url = "https://hub.spigotmc.org/nexus/content/repositories/releases/" }
                    ], "org.bukkit", "org.spigotmc:spigot-api", "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
                }
                mavenCentral()
                maven { url = "https://oss.sonatype.org/content/repositories/snapshots/" }
                maven { url = "https://oss.sonatype.org/content/repositories/central/" }
            }
            dependencies {
                compileOnly "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT"
            }
        """
            ).trimIndent(), """
                compileClasspath - Compile classpath for source set 'main'.
                \--- net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT FAILED
            """.trimIndent()
        )
        testWithoutExtension()
    }

    @Test
    fun `Test extension (list)`() {
        testWithWithoutExtension(
            String.format(
                """
            plugins {
                id "java"
                id "colosseum.gradle"
            }
            repositories {
                colosseum {
                    colosseumContent([
                        maven { url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/" },
                        maven { url = "https://hub.spigotmc.org/nexus/content/repositories/releases/" }
                    ], ["org.bukkit", "org.spigotmc:spigot-api", "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT"])
                }
                mavenCentral()
                maven { url = "https://oss.sonatype.org/content/repositories/snapshots/" }
                maven { url = "https://oss.sonatype.org/content/repositories/central/" }
            }
            dependencies {
                compileOnly "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT"
            }
        """
            ).trimIndent(), """
                compileClasspath - Compile classpath for source set 'main'.
                \--- net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT FAILED
            """.trimIndent()
        )
        testWithoutExtension()
    }
}