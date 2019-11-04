package com.julianjarecki.ettiketten.app.controller

import com.julianjarecki.ettiketten.app.ITabActions
import com.julianjarecki.ettiketten.view.base.AppTab
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tab
import tornadofx.Controller
import tornadofx.asObservable

class AppController : Controller(), ITabActions {
    val io by inject<IOController>()

    val activeTab = SimpleObjectProperty<AppTab>()
    var openedTabs = HashMap<Tab, AppTab>().asObservable()


    override fun export() {
        activeTab.value?.export()
    }

    override fun refreshData() {
        activeTab.value?.refreshData()
    }

    override fun saveData() {
        activeTab.value?.saveData()
    }

    override fun createNew() {
        activeTab.value?.createNew()
    }

    override fun deleteData() {
        activeTab.value?.deleteData()
    }
}