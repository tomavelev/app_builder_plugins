package com.programtom.appbuilder.app_builder_plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.programtom.appbuilder.app_builder_plugin.api.ProgramTomAPI
import com.programtom.appbuilder.app_builder_plugin.model.AppBuilderDataContainer

class AppBuilderAndCodeGeneratorSetupAction : AnAction() {

    companion object {
        var appBuilderDataContainer: AppBuilderDataContainer? = null
    }

    @Suppress("DialogTitleCapitalization")
    override fun actionPerformed(e: AnActionEvent) {
        appBuilderDataContainer = ProgramTomAPI.getCodeProviders()
        //TODO show picker
        Messages.showMessageDialog("Code Generators Updated", "Program Tom API", null)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }


    override fun update(e: AnActionEvent) {
        //TODO cache code providers and load them on update UI - so no API call is required
        if (appBuilderDataContainer == null) {
            e.presentation.text = "Setup Code Generators"
        } else {
            e.presentation.text = "Update Code Generators"
        }
    }
}
