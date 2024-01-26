package com.programtom.appbuilder.app_builder_plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.programtom.appbuilder.app_builder_plugin.api.ProgramTomAPI
import com.programtom.appbuilder.app_builder_plugin.model.Generator
import com.programtom.appbuilder.app_builder_plugin.model.NamedObject
import java.io.File
import java.util.regex.Pattern


class AppBuilderAndCodeGeneratorAction : AnAction() {

    private var selectedFile: VirtualFile? = null
    private var selectedGenerators = mutableSetOf<Generator>()
    override fun actionPerformed(e: AnActionEvent) {
        val file = File(selectedFile!!.path)

        if (selectedGenerators.size == 1) {
            if (file.isFile) {
                //THIS is IO operation. Should be looked out for handing FS
                val text = File(selectedFile!!.path).readText()
                generateCodeFromGenerator(e.project, selectedGenerators, text)
            } else {
                //TODO handle case for directory and with activators validating  text files in subdirectories
            }
        } else {
            if (file.isFile) {
                //THIS is IO operation. Should be looked out for handing FS
                val text = File(selectedFile!!.path).readText()

                @Suppress("UNCHECKED_CAST") val dialog =
                    ChooseGeneratorDialog(selectedGenerators as MutableSet<NamedObject>)
                val showAndGet = dialog.showAndGet()
                if (showAndGet) {
                    generateCodeFromGenerator(e.project, selectedGenerators, text)
                }

            } else {
                //TODO handle case for directory and with activators validating  text files in subdirectories
            }
        }

    }

    private fun generateCodeFromGenerator(project: Project?, generators: Set<Generator>?, text: String) {
        if (generators != null) {
            WriteCommandAction.runWriteCommandAction(project) {
                CommandProcessor.getInstance().executeCommand(
                    project, {
                        generators.forEach {
                            val dataModel = parseText(text, it)
                            if (dataModel.isBlank()) {
                                Messages.showMessageDialog("Parsing file failed", "Parse Error", null)
                            } else {
                                val code = generateCode(dataModel, it)
                                writeToIDE(code, it)
                            }
                        }
                    }, "Write Code ${generators.first().displayName} ${if (generators.size > 1) "and more..." else ""}", null
                )
            }
        }
    }

    private fun writeToIDE(code: String, selectedGenerator: Generator) {
        var newFile = selectedFile!!.path
        selectedGenerator.transformers.forEach {
            if (it.find != null && it.replace != null)
                newFile = newFile.replace(it.find, it.replace)
        }
        val file = File(newFile)
        if (file.exists()) {
            Messages.showMessageDialog("Already exists", "Duplicate Error", null)
        } else {
            FileUtil.writeToFile(file, code)
            VfsUtil.findFileByIoFile(file, true)
        }

    }

    private fun generateCode(dataModel: String, selectedGenerator: Generator): String {
        return ProgramTomAPI.generateCode(dataModel, selectedGenerator.generateUrl) ?: return ""
    }

    private fun parseText(text: String, selectedGenerator: Generator): String {
        return ProgramTomAPI.parseText(text, selectedGenerator.parseUrl) ?: return ""
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        selectedGenerators.clear()

        if (AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer != null) {
            selectedFile = e.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
            if (selectedFile == null) {
                return
            }
            val selectedFilePath = selectedFile!!.path +
                    if (selectedFile!!.isDirectory) "/" else ""

            val matchedActivators = mutableListOf<String>()

            AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer!!.activatorGroups.forEach { ag ->
                if (ag.id != null) {
                    var matchedAll = true

                    for (i in 0 until ag.generatorActivatorList.size) {
                        if (ag.generatorActivatorList[i].regex == null) {
                            matchedAll = false
                            break
                        }
                        //TODO check for requirements in sub-directory
                        // this will need potential parsing sub-directories
                        if (!getRegex(ag.generatorActivatorList[i].regex!!).matches(selectedFilePath)) {
                            matchedAll = false
                            break
                        }
                    }

                    if (matchedAll) {
                        matchedActivators.add(ag.id!!)
                    }
                }
            }

            if (matchedActivators.isNotEmpty()) {
                matchedActivators.forEach { activatorIds ->
                    AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer?.activatorGenerators?.get(
                        activatorIds
                    )
                        ?.let {
                            selectedGenerators.addAll(it)
                        }
                }
            }


            if (selectedGenerators.isNotEmpty()) {
                e.presentation.isVisible = true
                if (selectedGenerators.size == 1) {
                    e.presentation.text = selectedGenerators.first().displayName
                } else {
                    e.presentation.text = "Choose from ${selectedGenerators.size} Generators"
                }
            }


        }


    }

    companion object {
        private val map = mutableMapOf<String, Regex>()
        public fun getRegex(regex: String): Regex {
            if (map[regex] == null) {
                // Create a pattern and matcher
                val pattern = Pattern.compile(regex, Pattern.DOTALL)
                map[regex] =  pattern.toRegex()
            }
            return map[regex]!!
        }
    }
}