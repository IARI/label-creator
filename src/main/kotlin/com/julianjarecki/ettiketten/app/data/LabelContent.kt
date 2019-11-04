package com.julianjarecki.ettiketten.app.data

import javafx.beans.property.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel
import java.util.*

@Serializable
class LabelContent {
    val title = TextContent()
    @ContextualSerialization
    val enableSubTitle = SimpleBooleanProperty()

    val subTitle = TextContent()

    //@ContextualSerialization
    //val borderSize = SimpleDoubleProperty()

    val uuid = UUID.randomUUID().toString()

    @ContextualSerialization
    val linkedTo = SimpleStringProperty("")
}

class LabelContentModel : ItemViewModel<LabelContent>() {
    val title = bind(LabelContent::title)
    val enableSubTitle = bind<Boolean, SimpleBooleanProperty, SimpleBooleanProperty>(LabelContent::enableSubTitle, true)
    val subTitle = bind(LabelContent::subTitle)
    //val borderSize = bind(LabelContent::borderSize)
    val linkedTo = bind<String, SimpleStringProperty, SimpleStringProperty>(LabelContent::linkedTo)
}
