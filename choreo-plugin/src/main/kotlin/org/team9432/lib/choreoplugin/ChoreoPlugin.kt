package org.team9432.lib.choreoplugin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ChoreoPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generateChoreoConstants") { task ->
            task.doLast {
                val choreoDir = File(project.projectDir, "src/main/deploy/choreo")
                val trajFiles = choreoDir.listFiles()?.filter { it.extension == "traj" && !it.nameWithoutExtension.contains("""\.\d""".toRegex()) } ?: return@doLast

                val fileBuilder = FileSpec.builder("org.team9432.generated", "ChoreoPaths")

                val typeBuilder = TypeSpec.objectBuilder("ChoreoPaths")

                trajFiles.forEach { file ->
                    val prop = PropertySpec.builder(format(file.name), String::class)
                    prop.addModifiers(KModifier.CONST)
                    prop.initializer("\"${file.name.takeWhile { it != '.' }}\"")

                    typeBuilder.addProperty(prop.build())
                }

                fileBuilder.addType(typeBuilder.build())

                val constantsFile = fileBuilder.build()
                constantsFile.writeTo(File(project.projectDir, "src/main/kotlin/"))
            }
        }
    }

    private fun format(filename: String): String {
        val trajName = "Path" + filename.takeWhile { it != '.' }

        return trajName.map { if (it.isUpperCase() || it.isDigit()) "_$it" else it }
            .joinToString("")
            .drop(1) // Drop initial underscore
            .uppercase()
    }
}