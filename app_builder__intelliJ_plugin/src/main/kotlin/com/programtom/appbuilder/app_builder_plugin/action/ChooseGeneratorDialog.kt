package com.programtom.appbuilder.app_builder_plugin.action

import com.intellij.openapi.ui.DialogWrapper
import com.programtom.appbuilder.app_builder_plugin.model.NamedObject
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class ChooseGeneratorDialog(private val namedObjects: MutableSet<NamedObject>) : DialogWrapper(true) {

    private val allObjects: ArrayList<NamedObject> = ArrayList(namedObjects)
    override fun createCenterPanel(): JComponent {
        val jPanel = JPanel()
        jPanel.layout =
            GridLayout(allObjects.size, 1)
        allObjects.forEach { gen: NamedObject ->
            val checkbox = JCheckBox(gen.displayName, true)
            checkbox.addChangeListener {
                if ((it.source as JCheckBox).isSelected) {
                    namedObjects.add(gen)
                } else {
                    namedObjects.remove(gen)
                }
            }
            jPanel.add(checkbox)
        }
        return jPanel
    }

    init {
        @Suppress("DialogTitleCapitalization")
        title = "Select Generator(s)"
        setOKButtonText("Generate")
        isModal = true
        init()
    }

}