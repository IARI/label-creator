package com.julianjarecki.ettiketten.view.tabs

import com.julianjarecki.ettiketten.app.controller.appSettings
import com.julianjarecki.ettiketten.app.utils.folderfield
import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.app.utils.view
import com.julianjarecki.ettiketten.view.base.AppTab
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.scene.Parent
import tornadofx.*

class SettingsTab : AppTab("Settings", MaterialDesignIcon.SETTINGS.view) {
    val appPreferences by appSettings()

    override val root = vbox {
        form {
            fieldset("Paths", FontAwesomeIcon.FOLDER.view) {
                folderfield("Default document folder", appPreferences.documentFolder)
            }
            fieldset("Document") {
                field("Default Units") {
                    combobox(appPreferences.defaultUnits, Units.values().toList()) { }
                }
            }
            fieldset("Export") {
                field("Open Document after Export") {
                    checkbox("", appPreferences.openDocumentAfterExport)
                }
                field("Author") {
                    textfield(appPreferences.pdfAuthor)
                }
            }
        }
    }
}