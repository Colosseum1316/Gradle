package colosseum.gradle.unittest

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ColosseumGradlePluginKotlinTest {
    @TempDir
    lateinit var projectDir: File
    private lateinit var buildGradleKts: File
    private lateinit var settingsGradleKts: File

    @BeforeEach
    fun setup() {
        buildGradleKts = projectDir.resolve("build.gradle.kts")
        settingsGradleKts = projectDir.resolve("settings.gradle.kts")
    }

    private fun testWithWithoutExtension(buildGradleKtsContent: String, expectedOutput: String) {
        buildGradleKts.writeText(buildGradleKtsContent)
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
                id("java")
                id("colosseum.gradle")
            }
            repositories {
                mavenCentral()
                maven("https://oss.sonatype.org/content/repositories/snapshots")
                maven("https://oss.sonatype.org/content/repositories/central")
            }
            dependencies {
                compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
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
                id("java")
                id("colosseum.gradle")
            }
            repositories {
                colosseum {
                    colosseumContent(listOf(
                        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"),
                        maven("https://hub.spigotmc.org/nexus/content/repositories/releases/")
                    ), "org.bukkit", "org.spigotmc:spigot-api", "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
                }
                mavenCentral()
                maven("https://oss.sonatype.org/content/repositories/snapshots/")
                maven("https://oss.sonatype.org/content/repositories/central/")
            }
            dependencies {
                compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
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
                id("java")
                id("colosseum.gradle")
            }
            repositories {
                colosseum {
                    colosseumContent(listOf(
                        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"),
                        maven("https://hub.spigotmc.org/nexus/content/repositories/releases/")
                    ), listOf("org.bukkit", "org.spigotmc:spigot-api", "net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT"))
                }
                mavenCentral()
                maven("https://oss.sonatype.org/content/repositories/snapshots/")
                maven("https://oss.sonatype.org/content/repositories/central/")
            }
            dependencies {
                compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
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