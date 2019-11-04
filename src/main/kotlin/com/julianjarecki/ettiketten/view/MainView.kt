package com.julianjarecki.ettiketten.view

import com.julianjarecki.ettiketten.app.EttikettenApp.Companion.AppName
import com.julianjarecki.ettiketten.app.controller.appCtl
import com.julianjarecki.ettiketten.app.controller.appSettings
import com.julianjarecki.ettiketten.app.controller.io
import com.julianjarecki.ettiketten.app.data.LabelsDocument
import com.julianjarecki.ettiketten.app.utils.*
import com.julianjarecki.ettiketten.view.base.AppTab
import com.julianjarecki.ettiketten.view.tabs.KnownDocuments
import com.julianjarecki.ettiketten.view.tabs.SettingsTab
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.event.Event
import javafx.geometry.Orientation
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View(AppName) {
    val appCtl by appCtl()
    val io by io()
    val appPreferences by appSettings()

    var theTabPane: TabPane by singleAssign()

    override val root = borderpane {
        top {
            listmenu(Orientation.HORIZONTAL) {
                item("New", MaterialDesignIcon.NEW_BOX.view) {
                    setOnMouseClicked {
                        io.new()
                    }
                }
                item("Open", MaterialDesignIcon.BOOK_OPEN.view) {
                    activeItem = this
                    setOnMouseClicked {
                        chooseFile("Open a Document", arrayOf(labelsFilter)) {
                            initialDirectory = appPreferences.documentFolder.value
                        }.firstOrNull()?.let {
                            LabelsDocument().apply {
                                labelsFile.value = it
                                io.open(this)
                            }
                        }
                    }
                }
                separator(Orientation.VERTICAL)

                item("Save") {
                    setOnMouseClicked {
                        appCtl.saveData()
                    }
                }
                item("Export") {
                    setOnMouseClicked {
                        appCtl.export()
                    }
                }

                separator(Orientation.VERTICAL)

                item("Documents", MaterialDesignIcon.FILE_DOCUMENT.view) {
                    setOnMouseClicked {
                        openUi<KnownDocuments>()
                    }
                }
                item("Settings", MaterialDesignIcon.SETTINGS.view) {
                    setOnMouseClicked {
                        openUi<SettingsTab>()
                    }
                }
                /*
                item("Test") {
                    setOnMouseClicked {
                        find<Test>().openWindow()
                    }
                }
                */
            }
        }
        center {
            theTabPane = tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.ALL_TABS
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
            }
        }
    }

    init {
        subscribe<OpenUiComponent> { ev ->
            val openTab = appCtl.openedTabs.toList().firstOrNull { (_, a) ->
                a instanceOf ev.cls && a.hasSameScrope(ev.tabscope)
            }
            if (openTab != null) {
                runLater {
                    openTab.second.append(ev.tabscope)
                    theTabPane.selectionModel.select(openTab.first)
                }
                return@subscribe
            }
            val component = find(ev.cls, ev.tabscope)
            theTabPane.newTab(component)
        }

    }

    override fun onDock() {
        openUi<KnownDocuments>()

        root.addEventFilter(KeyEvent.KEY_PRESSED) { ev ->
            if (ev.isShortcutDown)
                when (ev.code) {
                    //KeyCode.DELETE -> deleteSelected()
                    KeyCode.TAB -> {
                        if (ev.isShiftDown) theTabPane.circlePrev()
                        else theTabPane.circleNext()
                        ev.consume()
                    }
                    KeyCode.W -> {
                        theTabPane.selectionModel.selectedItem?.apply {
                            Event.fireEvent(this, Event(Tab.CLOSED_EVENT))
                            close()
                            ev.consume()
                        }
                    }
                    else -> {
                    }
                }

            appCtl.activeTab.value?.keyboardEvent(ev)
        }

        appCtl.activeTab.bind(theTabPane.selectionModel.selectedItemProperty().objectBinding(op = appCtl.openedTabs::get))
    }

    fun TabPane.newTab(c: AppTab) {
        tab(c) {
            appCtl.openedTabs.put(this, c)
            setOnClosed {
                log.info("Closed ${c.title}")
                appCtl.openedTabs.remove(this)
            }
            //if (c is AppTab) c.keyboardShortcuts(this)
            selectionModel.select(this)
        }
    }
}