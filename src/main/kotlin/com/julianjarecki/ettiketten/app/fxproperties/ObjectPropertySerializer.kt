package com.julianjarecki.ettiketten.app.fxproperties

import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.*

@Serializer(forClass = SimpleObjectProperty::class)
open class ObjectPropertySerializer<T : Any>(val dataSerializer: KSerializer<T>) : KSerializer<SimpleObjectProperty<T>> {
    override val descriptor: SerialDescriptor = dataSerializer.descriptor

    override fun deserialize(decoder: Decoder): SimpleObjectProperty<T> = dataSerializer
            .deserialize(decoder)
            .let(::SimpleObjectProperty)

    override fun patch(decoder: Decoder, old: SimpleObjectProperty<T>): SimpleObjectProperty<T> = old.apply {
        value = dataSerializer
                .deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, obj: SimpleObjectProperty<T>) {
        dataSerializer
/*
                .apply {
                    Logger.getLogger("SimpleObjectProperty").info("serialize $this")
                }
*/
                .serialize(encoder, obj.value)
    }
}
