@file:Suppress("IntentionDescriptionNotFoundInspection")

package com.programtom.appbuilder.app_builder_plugin.action

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.programtom.appbuilder.app_builder_plugin.api.ProgramTomAPI
import com.programtom.appbuilder.app_builder_plugin.model.ActivatorGroup
import com.programtom.appbuilder.app_builder_plugin.model.CodeProvider
import com.programtom.appbuilder.app_builder_plugin.model.ExistingFileCodeAssist
import com.programtom.appbuilder.app_builder_plugin.model.NamedObject

class ExistingFileAction : PsiElementBaseIntentionAction(), IntentionAction {

    private var fileCodeAssistSet = mutableSetOf<ExistingFileCodeAssist>()

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {

        if (fileCodeAssistSet.size == 1) {
            improveCode(fileCodeAssistSet.first(), project, editor)
        } else {
            @Suppress("UNCHECKED_CAST") val dialog = ChooseGeneratorDialog(fileCodeAssistSet as MutableSet<NamedObject>)
            val showAndGet = dialog.showAndGet()
            if (showAndGet) {
                improveCode(fileCodeAssistSet.first(), project, editor)
            }
        }

    }

    private fun improveCode(
        existingFileCodeAssist: ExistingFileCodeAssist, project: Project, editor: Editor
    ) {

        WriteCommandAction.runWriteCommandAction(project) {
            CommandProcessor.getInstance().executeCommand(
                project, {
                    val code = ProgramTomAPI.improveCode(existingFileCodeAssist, editor.document.text)
                    editor.document.setText(code.trim())
                }, "Improve Existing Code ${existingFileCodeAssist.displayName}", null
            )
        }
    }

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        fileCodeAssistSet.clear()

        if (AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer != null) {
            val currentFile = getCurrentFile(project, editor)

            if (currentFile == null) {
                return false
            } else {
                val matchedActivators = mutableListOf<String>()
                val matchedGroupsMap = mutableMapOf<String, ActivatorGroup>()

                AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer!!.activatorGroups.forEach { ag ->
                    if (ag.id != null) {

                        val matchedAll = matchActivators(ag, currentFile.virtualFile.path)

                        matchedGroupsMap[ag.id!!] = ag
                        if (matchedAll) {
                            matchedActivators.add(ag.id!!)
                        }
                    }
                }

                if (matchedActivators.isNotEmpty()) {
                    AppBuilderAndCodeGeneratorSetupAction.appBuilderDataContainer!!.codeProviders.forEach { codeProvider: CodeProvider ->
                        codeProvider.existingFileCodeAssists.forEach { existingFileCodeAssist: ExistingFileCodeAssist ->
                            if (existingFileCodeAssist.activatorGroupId != null && matchedActivators.contains(
                                    existingFileCodeAssist.activatorGroupId
                                )
                            ) {
                                fileCodeAssistSet.add(existingFileCodeAssist)
                            }
                        }
                    }
                }


                if (fileCodeAssistSet.isNotEmpty()) {
                   val list = arrayListOf<ExistingFileCodeAssist>();
                    list.addAll(fileCodeAssistSet)

                    for (i in list.indices.reversed()) {
                        val fileCodeAssist = list[i]
                        val listActivatorGroup = mutableListOf<ActivatorGroup>()

                        if (fileCodeAssist.inlineActivatorGroupId != null && matchedGroupsMap[fileCodeAssist.inlineActivatorGroupId] != null) {
                            listActivatorGroup.add(matchedGroupsMap[fileCodeAssist.inlineActivatorGroupId]!!)
                        }

                        if (fileCodeAssist.lineActivatorGroupId != null && matchedGroupsMap[fileCodeAssist.lineActivatorGroupId] != null) {
                            listActivatorGroup.add(matchedGroupsMap[fileCodeAssist.lineActivatorGroupId]!!)
                        }

                        if (listActivatorGroup.isNotEmpty()) {
                            var matchesAllGroups = true
                            listActivatorGroup.forEach {
                                if (!matchActivators(it, editor.document.text)) {
                                    matchesAllGroups = false
                                }
                            }

                            if (!matchesAllGroups) {
                                list.removeAt(i)
                            }
                        } else {
                            list.removeAt(i)
                        }
                    }

                    return list.isNotEmpty()
                }

            }

        }
        return false
    }

    private fun matchActivators(
        ag: ActivatorGroup, content: String
    ): Boolean {
        var matchedAll = true
        for (i in 0 until ag.generatorActivatorList.size) {
            if (ag.generatorActivatorList[i].regex == null) {
                matchedAll = false
                break
            }
            if (!AppBuilderAndCodeGeneratorAction.getRegex(ag.generatorActivatorList[i].regex!!).matches(content)
            //TODO improve this by adding parameter in the activator - for what stage should be checked and minimize iterations
            ) {
                matchedAll = false
                break
            }
        }
        return matchedAll
    }

    override fun getText(): String {
        return familyName
    }

    override fun getFamilyName(): @IntentionFamilyName String {

        return if (fileCodeAssistSet.isEmpty()) {
            "No Code Assist"
        } else if (fileCodeAssistSet.size == 1) {
            fileCodeAssistSet.first().name!!
        } else {
            "Multiple Code Assistants (TODO)"
        }
    }

    private fun getCurrentFile(project: Project, editor: Editor): PsiFile? {
        return PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
    }
}
