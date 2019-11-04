package com.julianjarecki.ettiketten.app.fxproperties

import com.julianjarecki.ettiketten.app.utils.userHomeDir
import com.julianjarecki.ettiketten.app.utils.absolutePath
import com.julianjarecki.ettiketten.app.utils.stringconverter
import com.julianjarecki.ettiketten.app.utils.toFile
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.*
import java.io.File
import java.nio.file.Path

@Serializable
class FileProperty(@Transient private val f: File = userHomeDir) : SimpleObjectProperty<File>(f) {
    constructor(f: String) : this(f.toFile)
    constructor(f: Path) : this(f.toFile())

    @Transient
    val absolutePath = absolutePath()

    @Transient
    val name = SimpleStringProperty(f.name).apply {
        bindBidirectional(this@FileProperty, converter)
    }

    fun resolve(relPath: String) = value.toPath().resolve(relPath).toFile()

    @Serializer(forClass = FileProperty::class)
    companion object : KSerializer<FileProperty> {
        override fun deserialize(decoder: Decoder) = FileProperty(decoder.decodeString().toFile)

        override fun serialize(encoder: Encoder, obj: FileProperty) {
            encoder.encodeString(obj.value?.absolutePath ?: "NULL")
        }

        @Transient
        val converter = File::getAbsolutePath stringconverter String::toFile
    }
}