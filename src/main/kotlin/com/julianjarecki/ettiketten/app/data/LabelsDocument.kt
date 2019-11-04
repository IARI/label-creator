package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.ettiketten.app.fxproperties.FileProperty
import com.julianjarecki.ettiketten.app.fxproperties.jfxJsonSerializer
import com.julianjarecki.ettiketten.app.fxproperties.jfxJsonSerializerNonstrict
import com.julianjarecki.ettiketten.app.utils.loadSettings
import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import tornadofx.ItemViewModel

@Serializable
class LabelsDocument {
    val labelsFile = FileProperty()

    @Transient
    val data = SimpleObjectProperty<LabelsDocumentData>()

    fun load() {
        labelsFile.value.loadSettings("Labels Document", LabelsDocumentData.serializer()) {
            this@LabelsDocument.data.value = this
        }
    }

    fun save() {
        val dataString = jfxJsonSerializer.stringify(LabelsDocumentData.serializer(), data.value)
        labelsFile.value.writeText(dataString)
    }
}

class LabelsDocumentModel : ItemViewModel<LabelsDocument>() {
    val labelsFile = bind(LabelsDocument::labelsFile)
    val data = bind(LabelsDocument::data)

    fun load() = item.load()
    fun save() = item.save()
}
