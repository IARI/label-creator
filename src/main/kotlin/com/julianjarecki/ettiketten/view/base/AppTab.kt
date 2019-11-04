package com.julianjarecki.ettiketten.view.base

import com.julianjarecki.ettiketten.app.ITabActions
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.Fragment
import tornadofx.Scope

abstract class AppTab(title: String, icon: Node? = null) : Fragment(title, icon), ITabActions {
    open fun keyboardEvent(event: KeyEvent) {
        if (event.isShortcutDown) {
            when (event.code) {
                KeyCode.R -> refreshData()
                KeyCode.S -> saveData()
                KeyCode.N -> createNew()
                KeyCode.E -> export()
                else -> Unit
            }
        } else when (event.code) {
            KeyCode.DELETE -> deleteData()
            else -> Unit
        }
    }

    open fun hasSameScrope(newScope: Scope) = true
    open fun append(scope: Scope) {}

    override fun export() {}
    override fun refreshData() {}
    override fun saveData() {}
    override fun createNew() {}
    override fun deleteData() {}
}