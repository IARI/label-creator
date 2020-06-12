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
            fieldset("Rendering") {
                field("auto-height factor") {
                    tooltip("Fraction of Labelheight that determines auto-fontheight")
                    spinner(
                        .01, 1.2, amountToStepBy = .01, editable = true,
                        enableScroll = true, property = appPreferences.labelFontheightFraction
                    )
                }
                field("line height") {
                    tooltip("multiple of the line-height that determines the distance between two lines")
                    spinner(
                        .05, 5.0, amountToStepBy = .05, editable = true,
                        enableScroll = true, property = appPreferences.multipliedLeading
                    )
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