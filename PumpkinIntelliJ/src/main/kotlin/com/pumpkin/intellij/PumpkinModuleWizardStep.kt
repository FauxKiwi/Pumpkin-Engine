package com.pumpkin.intellij

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import javax.swing.JComponent

class PumpkinModuleWizardStep : ModuleWizardStep() {
    private val newProjectPanel = PumpkinNewProjectPanel()

    override fun getComponent(): JComponent = newProjectPanel.apply { attachTo(this) }

    override fun updateDataModel() {
    }
}