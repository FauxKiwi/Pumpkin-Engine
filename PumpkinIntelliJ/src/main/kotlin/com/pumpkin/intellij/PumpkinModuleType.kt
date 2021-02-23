package com.pumpkin.intellij

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.IconLoader
import javax.swing.*

class PumpkinModuleType : ModuleType<PumpkinModuleBuilder>(ID) {
    companion object {
        private const val ID = "PUMPKIN_MODULE_TYPE"

        val INSTANCE by lazy { ModuleTypeManager.getInstance().findByID(ID) as PumpkinModuleType }
    }

    override fun createModuleBuilder() = PumpkinModuleBuilder()

    override fun getName() = "Pumpkin Project"

    override fun getDescription() = "Pumpkin engine Project"

    override fun getNodeIcon(isOpened: Boolean): Icon = IconLoader.getIcon("/icons/pumpkin.ico")

    override fun createWizardSteps(
        wizardContext: WizardContext,
        moduleBuilder: PumpkinModuleBuilder,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider)
    }
}