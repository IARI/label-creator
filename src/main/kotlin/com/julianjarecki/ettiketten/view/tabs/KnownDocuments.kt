package com.julianjarecki.ettiketten.view.tabs

import com.julianjarecki.ettiketten.app.controller.appSettings
import com.julianjarecki.ettiketten.app.controller.io
import com.julianjarecki.ettiketten.app.data.LabelsDocumentDataModel
import com.julianjarecki.ettiketten.app.data.LabelsDocumentModel
import com.julianjarecki.ettiketten.app.utils.inmodel
import com.julianjarecki.ettiketten.app.utils.openNewScope
import com.julianjarecki.ettiketten.app.utils.openUi
import com.julianjarecki.ettiketten.app.utils.view
import com.julianjarecki.ettiketten.view.base.AppTab
import com.julianjarecki.ettiketten.view.fragments.DocumentFragment
import com.julianjarecki.tfxserializer.utils.OS
import com.julianjarecki.tfxserializer.utils.openInExplorer
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.scene.layout.Priority
import tornadofx.*


class KnownDocuments : AppTab("Documents", MaterialDesignIcon.FILE_DOCUMENT.view) {
    val appPreferences by appSettings()
    val io by io()

    val selectedDocument by inject<LabelsDocumentModel>()
    //var theList by singleAssign<ListView<LabelsDocument>>()

    override val root = vbox {
        //theList =
        listview(appPreferences.knownDocuments) {
            bindSelected(selectedDocument)
            prefWidth = 0.0
            usePrefHeight = false
            vgrow = Priority.ALWAYS

            cellFragment(DocumentFragment::class)

            onUserSelect(action = io::open)

            contextmenu {
                item("open", graphic = FontAwesomeIcon.FOLDER_OPEN.view).action {
                    selectedItem?.let(io::open)
                }
                item("clone", graphic = FontAwesomeIcon.COPY.view).action {
                    selectedItem?.let(io::copy)
                }
                item("show in Browser", graphic = FontAwesomeIcon.FOLDER.view).action {
                    selectedItem?.labelsFile?.value?.openInExplorer()
                }
                item("delete", graphic = FontAwesomeIcon.TRASH.view).action {
                    selectedItem?.let(io::delete)
                }
                item("rename", graphic = FontAwesomeIcon.TRASH.view).action {
                    selectedItem?.let(io::rename)
                }
            }

            //copy
        }
    }

    override fun createNew() {
        io.new()
    }

    override fun deleteData() {
        val f = selectedDocument.labelsFile.value
        val doc = selectedDocument.item
        confirm("Delete file?", "Really delete $f?") {
            f.delete()
            appPreferences.knownDocuments.value.remove(doc)
        }
    }
}