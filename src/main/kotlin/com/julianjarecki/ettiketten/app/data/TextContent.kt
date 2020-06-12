package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.tfxserializer.fxproperties.ColorProperty
import com.julianjarecki.tfxserializer.utils.bindCount
import javafx.beans.property.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.*

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
    val lines = observableListOf<String>().apply {
        text.onChange {
            if (it == null) clear()
            else setAll(it.split("\n"))
        }
    }
    val multiline = lines.sizeProperty.ge(2)
    val autoSize = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(TextContent::autoSize, true)
    val size = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(TextContent::size, true)
    val color = bind(TextContent::color, true)
}
