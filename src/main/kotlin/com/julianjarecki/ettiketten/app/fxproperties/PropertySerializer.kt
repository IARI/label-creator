package com.julianjarecki.ettiketten.app.fxproperties

import javafx.beans.property.*
import kotlinx.serialization.*
import kotlinx.serialization.context.SimpleModule
import kotlinx.serialization.json.Json
import java.io.File

@Serializer(forClass = SimpleStringProperty::class)
open class StringPropertySerializer : KSerializer<SimpleStringProperty> {
    override fun deserialize(decoder: Decoder) = SimpleStringProperty(decoder.decodeString())

    override fun serialize(encoder: Encoder, obj: SimpleStringProperty) {
        encoder.encodeString(obj.value ?: "NULL")
    }
}

@Serializer(forClass = SimpleIntegerProperty::class)
open class IntPropertySerializer : KSerializer<SimpleIntegerProperty> {
    override fun deserialize(decoder: Decoder) = SimpleIntegerProperty(decoder.decodeInt())

    override fun serialize(encoder: Encoder, obj: SimpleIntegerProperty) {
        encoder.encodeInt(obj.value ?: 0)
    }
}

@Serializer(forClass = SimpleBooleanProperty::class)
open class BoolPropertySerializer : KSerializer<SimpleBooleanProperty> {
    override fun deserialize(decoder: Decoder) = SimpleBooleanProperty(decoder.decodeBoolean())

    override fun serialize(encoder: Encoder, obj: SimpleBooleanProperty) {
        encoder.encodeBoolean(obj.value ?: false)
    }
}

@Serializer(forClass = SimpleDoubleProperty::class)
open class DoublePropertySerializer : KSerializer<SimpleDoubleProperty> {
    override fun deserialize(decoder: Decoder) = SimpleDoubleProperty(decoder.decodeDouble())

    override fun serialize(encoder: Encoder, obj: SimpleDoubleProperty) {
        encoder.encodeDouble(obj.value ?: .0)
    }
}

@Serializer(forClass = SimpleFloatProperty::class)
open class FloatPropertySerializer : KSerializer<SimpleFloatProperty> {
    override fun deserialize(decoder: Decoder) = SimpleFloatProperty(decoder.decodeFloat())

    override fun serialize(encoder: Encoder, obj: SimpleFloatProperty) {
        encoder.encodeFloat(obj.value ?: 0f)
    }
}

val simpleStringModule = SimpleModule(SimpleStringProperty::class, StringPropertySerializer())
val simpleIntModule = SimpleModule(SimpleIntegerProperty::class, IntPropertySerializer())
val simpleBoolModule = SimpleModule(SimpleBooleanProperty::class, BoolPropertySerializer())
val simpleDoubleModule = SimpleModule(SimpleDoubleProperty::class, DoublePropertySerializer())
val simpleFloatModule = SimpleModule(SimpleFloatProperty::class, FloatPropertySerializer())

val jfxModules = listOf(
    simpleStringModule,
    simpleIntModule,
    simpleBoolModule,
    simpleDoubleModule,
    simpleFloatModule
)

fun <T : SerialFormat> T.installJfxModules(): T = apply {
    jfxModules.forEach { install(it) }
}

val jfxJsonSerializer by lazy {
    Json(indented = true)
        .installJfxModules()
}
val jfxJsonSerializerNonstrict by lazy {
    Json(indented = true, strictMode = false)
        .installJfxModules()
}

fun <T : Any> Property<T>.readFrom(text: String, deserializer: DeserializationStrategy<T>, strict: Boolean = false) {
    value = (if (strict) jfxJsonSerializer else jfxJsonSerializerNonstrict).parse(deserializer, text)
}

fun <T : Any> Property<T>.readFrom(file: File, deserializer: DeserializationStrategy<T>, strict: Boolean = false) =
    readFrom(file.readText(), deserializer, strict)
