package colosseum.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.InclusiveRepositoryContentDescriptor
import java.util.function.*

open class ColosseumExtension(private val project: Project) {
    protected open fun colosseumAddRepositories(repositories: Collection<ArtifactRepository>) {
        project.repositories.addAll(repositories)
    }

    protected open fun colosseumContent(repositories: Collection<ArtifactRepository>, fullModuleNames: Supplier<Collection<String>>, filterAction: BiConsumer<InclusiveRepositoryContentDescriptor, Collection<String>>) {
        colosseumAddRepositories(repositories)
        return project.repositories.exclusiveContent {
            forRepositories(*repositories.toTypedArray())
            filter {
                filterAction.accept(this, fullModuleNames.get())
            }
        }
    }

    open fun colosseumContent(repositories: Collection<ArtifactRepository>, vararg modules: String) {
        return colosseumContent(repositories, modules.toList())
    }

    open fun colosseumContent(repositories: Collection<ArtifactRepository>, fullModuleName: Collection<String>) {
        return colosseumContent(repositories, { fullModuleName }, { inclusive, names ->
            for (module in names) {
                val objs = module.split(":")
                when (objs.size) {
                    1 -> inclusive.includeGroup(objs[0])
                    2 -> inclusive.includeModule(objs[0], objs[1])
                    3 -> inclusive.includeVersion(objs[0], objs[1], objs[2])
                    else -> throw IllegalArgumentException("Invalid module name input: $module")
                }
            }
        })
    }
}