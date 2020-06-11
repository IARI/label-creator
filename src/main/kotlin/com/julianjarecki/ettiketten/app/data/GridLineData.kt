package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.app.utils.itext.convertUnits
import com.julianjarecki.tfxserializer.app.fxproperties.ObjectPropertySerializer
import javafx.beans.property.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel

@Serializable
class GridLineData {
    @ContextualSerialization
    val enableUnit = SimpleBooleanProperty(false)

    @ContextualSerialization
    val size = SimpleDoubleProperty(1.0)

    @Serializable(with = ObjectPropertySerializer::class)
    val units = SimpleObjectProperty<Units>(Units.Millimeter)
}

class GridLineDataModel : ItemViewModel<GridLineData>() {
    val enableUnit = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(GridLineData::enableUnit, true)
    val units = bind<Units, SimpleObjectProperty<Units>, SimpleObjectProperty<Units>>(GridLineData::units, true)
    val size = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(GridLineData::size, true)
    val sizeU = size.convertUnits(units)
}
