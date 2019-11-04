package com.julianjarecki.ettiketten.app.fxproperties

import javafx.collections.ObservableList
import kotlinx.serialization.*
import tornadofx.*

@Serializer(forClass = ObservableList::class)
class ObservableListSerializer<T : Any>(val dataSerializer: KSerializer<T>) : KSerializer<ObservableList<T>> {
    private val listSerializer = dataSerializer.list
    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun deserialize(decoder: Decoder): ObservableList<T> = listSerializer.deserialize(decoder).asObservable()

    override fun serialize(encoder: Encoder, obj: ObservableList<T>) = listSerializer.serialize(encoder, obj)
}