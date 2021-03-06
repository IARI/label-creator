package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.tfxserializer.app.fxproperties.jfxJsonSerializer
import com.julianjarecki.tfxserializer.app.fxproperties.jfxJsonSerializerNonstrict
import com.julianjarecki.tfxserializer.fxproperties.FileProperty
import com.julianjarecki.tfxserializer.utils.loadSettings
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
        jfxJsonSerializerNonstrict.parse(LabelsDocumentData.serializer(), labelsFile.value.readText())
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
