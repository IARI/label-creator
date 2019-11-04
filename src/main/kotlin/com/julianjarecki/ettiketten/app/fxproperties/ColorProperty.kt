package com.julianjarecki.ettiketten.app.fxproperties

import com.julianjarecki.ettiketten.app.utils.html
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import kotlinx.serialization.*

@Serializable
class ColorProperty(@Transient private val r: Color = Color.BLACK) : SimpleObjectProperty<Color>(r) {
    @Serializer(forClass = ColorProperty::class)
    companion object {
        override fun deserialize(decoder: Decoder): ColorProperty {
            return ColorProperty(Color.web(decoder.decodeString()))
        }

        override fun serialize(encoder: Encoder, obj: ColorProperty) {
            (obj.value ?: Color.BLACK).let {
                encoder.encodeString(it.html)
            }
        }
    }
}