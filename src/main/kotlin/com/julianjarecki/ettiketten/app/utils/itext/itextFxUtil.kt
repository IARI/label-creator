package com.julianjarecki.ettiketten.app.utils.itext

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.layout.element.BlockElement
import com.julianjarecki.tfxserializer.utils.BidirectionalConversionBinding
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.paint.Color
import tornadofx.onChange

val Color.iText get() = DeviceRgb(red.toFloat(), green.toFloat(), blue.toFloat())

var BlockElement<*>.fontColor: Color
    get() = throw NotImplementedError()
    set(value) {
        setFontColor(value.iText, value.opacity.toFloat())
    }


class PointConversionBinding(pointProp: Property<Number>, prop: Property<Number>, val unit: ObservableValue<Units>) :
    BidirectionalConversionBinding<Number, Number>(pointProp, prop) {
    override fun convertStoT(s: Number): Number = s.toDouble() / (unit.value?.points ?: 1.0)
    override fun convertTtoS(t: Number): Number = t.toDouble() * (unit.value?.points ?: 1.0)
    val unitChange = ChangeListener<Units> { p, old, new ->
        triggerUpdate()
    }

    override fun bind() {
        super.bind()
        unit.addListener(unitChange)
    }

    override fun unbind() {
        super.unbind()
        unit.removeListener(unitChange)
    }
}

//PointConversionBinding

class ConvertedDoubleProperty(val unit: ObservableValue<Units>, sourceProp: Property<Number>) :
    SimpleDoubleProperty() {
    val binding: PointConversionBinding = PointConversionBinding(sourceProp, this, unit)

    init {
        binding.bind()
    }
}


fun Property<Number>.convertUnits(unit: ObservableValue<Units>) = ConvertedDoubleProperty(unit, this)