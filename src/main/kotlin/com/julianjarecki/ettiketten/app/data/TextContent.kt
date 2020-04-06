package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.tfxserializer.fxproperties.ColorProperty
import javafx.beans.property.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel

@Serializable
class TextContent {
    @ContextualSerialization
    val text = SimpleStringProperty("")
    @ContextualSerialization
    val autoSize = SimpleBooleanProperty(true)
    @ContextualSerialization
    val size = SimpleDoubleProperty(12.0)
    val color = ColorProperty()
/*
    enum class alignment {
        LEFT, RIGHT, CENTERED
    }
*/
}

class TextContentModel : ItemViewModel<TextContent>() {
    val text = bind(TextContent::text, true)
    val autoSize = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(TextContent::autoSize, true)
    val size = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(TextContent::size, true)
    val color = bind(TextContent::color, true)
}
