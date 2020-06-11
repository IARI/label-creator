package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.LabelsDocument
import com.julianjarecki.ettiketten.app.data.LabelsDocumentData
import com.julianjarecki.ettiketten.app.data.LabelsDocumentModel
import com.julianjarecki.tfxserializer.utils.absolutePath
import javafx.beans.property.ObjectProperty
import tornadofx.*

class DocumentFragment : ListCellFragment<LabelsDocument>() {
    val document = LabelsDocumentModel().bindTo(this)

    override val root = hbox {
        label(document.labelsFile.absolutePath())

        spacer()

        hbox {
            visibleWhen((document.data as ObjectProperty<LabelsDocumentData>).isNotNull)

            label(document.data.select { it.columns.sizeProperty })
            label(" (x ")
            label(document.data.select(LabelsDocumentData::count))
            label(")")
        }
    }
}