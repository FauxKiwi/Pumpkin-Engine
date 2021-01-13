package com.pumpkin.editor.project

import com.pumpkin.editor.editorLogger
import java.io.File
import java.io.FileWriter

object ProjectStructure {

    /*
    Root
    |-MyProject.peproj
    |-Assets
      |-MyScene.pumpkin
      |-...
    |-Sources
      |-generated
        |-com/company
          |-MyProject.kt <--Application
          |-...
        |-com/pumpkin
          |-EntryPoint.kt
      //|-engine
        //|-com/pumpkin
          //|-//Library sources
    |-Gradle
      |-build.gradle
      |-settings.gradle
      |-gradle.properties
      |-gradlew
      |-gradlew.bat
      |-wrapper
        |-gradle-wrapper.jar
        |-gradle-wrapper.properties
     */

    fun generateStructure(name: String, companyID: String, path: String): Boolean {
        val directory = File(path)
        if (!directory.exists()) {
            editorLogger.info("The specified project directory does not exist, so it will be created")
            directory.mkdir()
        }
        if (!directory.isDirectory) {
            editorLogger.warn("The specified path does not point to a directory")
            return false
        }
        if (directory.listFiles()?.isNotEmpty() == true) {
            editorLogger.warn("The specified project directory is not empty")
            return false
        }

        generateFoldersAndFiles(name, companyID, directory)

        return true
    }

    private fun generateFoldersAndFiles(projectName: String, companyID: String, rootDir: File) {
        val rdp = rootDir.path

        FileWriter(File("$rdp/$projectName.peproj").also { it.createNewFile() }).use { w ->
            w.append("""
                {
                    "Project": "$projectName",
                    "Scenes": [
                    ],
                    "Settings": {
                        "EditorCamera": {
                        }
                    }
                }
            """.trimIndent())
        }

        val assets = File("$rdp/Assets").also { it.mkdir() }

        FileWriter(File("${assets.path}/UntitledScene.pumpkin").also { it.createNewFile() }).use { w ->
            w.append("{\r\n" +
                    "    \"Scene\": \"UntitledScene\",\r\n" +
                    "    \"Entities\": [\r\n" +
                    "    ]\r\n" +
                    "}")
        }

        val sources = File("$rdp/Sources").also { it.mkdir() }
        val genSources = File("${sources.path}/generated").also { it.mkdirs() }

        val entryPointDir = File("${genSources.path}/com/pumpkin").also { it.mkdirs() }
        FileWriter(File("$entryPointDir/EntryPoint.kt").also { it.createNewFile() }).use { w ->
            w.append("package com.pumpkin\r\n\r\n" +
                    "fun runApplication() {\r\n" +
                    "\t")
            w.append(companyID)
            w.append(".main()\r\n" +
                    "}")
        }

        val projectSrcDir = File("${genSources.path}/${companyID.replace('.', '/')}").also { it.mkdirs() }
        FileWriter(File("$projectSrcDir/$projectName.kt").also { it.createNewFile() }).use { w ->
            w.append("package ")
            w.append(companyID)
            w.append("\r\n\r\n" +
                    "fun main() {\r\n" +
                    "\tcom.pumpkin.core.Application.set(")
            w.append(projectName)
            w.append("())\r\n" +
                    "}\r\n\r\n" +
                    "class ")
            w.append(projectName)
            w.append(" : com.pumpkin.core.Application() {\r\n" +
                    "\toverride fun init() {\r\n" +
                    "\t\tpushLayer(ProjectLayer())\r\n" +
                    "\t}\r\n" +
                    "}\r\n\r\n" +
                    "class ProjectLayer : com.pumpkin.core.layer.Layer() {\r\n" +
                    "}")
        }
    }
}