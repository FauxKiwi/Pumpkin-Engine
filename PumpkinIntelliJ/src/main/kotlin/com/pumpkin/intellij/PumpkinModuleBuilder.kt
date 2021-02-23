package com.pumpkin.intellij

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType

class PumpkinModuleBuilder : ModuleBuilder() {
    override fun getModuleType(): ModuleType<*> = PumpkinModuleType.INSTANCE

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep =
        PumpkinModuleWizardStep()
}