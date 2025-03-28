package colosseum.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ColosseumGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("colosseum", ColosseumExtension::class.java, target)
    }
}