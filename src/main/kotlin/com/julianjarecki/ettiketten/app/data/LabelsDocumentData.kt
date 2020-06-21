package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.ettiketten.app.utils.itext.BorderStyle
import com.julianjarecki.ettiketten.app.utils.itext.Fonts
import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.app.utils.itext.convertUnits
import com.julianjarecki.tfxserializer.app.fxproperties.ObjectPropertySerializer
import com.julianjarecki.tfxserializer.app.fxproperties.ObservableListSerializer
import com.julianjarecki.tfxserializer.fxproperties.ColorProperty
import com.julianjarecki.tfxserializer.utils.bindCount
import javafx.beans.property.*
import javafx.scene.paint.Color
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.observableMapOf
import tornadofx.onChange

@Serializable
class LabelsDocumentData {
    @ContextualSerialization
    val title = SimpleStringProperty("Labels")
    val pageSize = PageSize()

    @Serializable(with = ObjectPropertySerializer::class)
    val units = SimpleObjectProperty<Units>(Units.Millimeter)

    @Serializable(with = ObservableListSerializer::class)
    @SerialName("columnDefinitions")
    val columns = observableListOf<GridLineData>()

    @Serializable(with = ObservableListSerializer::class)
    @SerialName("rowDefinitions")
    val rows = observableListOf<GridLineData>()

    @ContextualSerialization
    val count = SimpleIntegerProperty(2)

    @ContextualSerialization
    val centerH = SimpleBooleanProperty(true)

    @ContextualSerialization
    val centerV = SimpleBooleanProperty(true)

    @ContextualSerialization
    val offsetH = SimpleDoubleProperty(.0)

    @ContextualSerialization
    val offsetV = SimpleDoubleProperty(.0)

    @ContextualSerialization
    val controlLabelH = SimpleBooleanProperty(false)

    @ContextualSerialization
    val controlLabelV = SimpleBooleanProperty(false)

    @ContextualSerialization
    val labelWidth = SimpleDoubleProperty(.0)

    @ContextualSerialization
    val labelHeight = SimpleDoubleProperty(.0)

    @Serializable(with = ObservableListSerializer::class)
    val data = observableListOf<LabelContent>()

    @Serializable(with = ObjectPropertySerializer::class)
    val font = SimpleObjectProperty<Fonts>(Fonts.HELVETICA)

    @ContextualSerialization
    val autoFontSizeMod = SimpleDoubleProperty(.0)

    @ContextualSerialization
    val drawBorder = SimpleBooleanProperty()

    @ContextualSerialization
    val drawCircle = SimpleBooleanProperty()

    @ContextualSerialization
    val markCenter = SimpleBooleanProperty()

    @ContextualSerialization
    val borderInside = SimpleBooleanProperty()

    @ContextualSerialization
    val borderWidth = SimpleDoubleProperty(1.0)
    val borderColor = ColorProperty()

    @Serializable(with = ObjectPropertySerializer::class)
    val borderStyle = SimpleObjectProperty<BorderStyle>(BorderStyle.Solid)

    fun syncCreateData(goal: Int = count.value) {
        val current = data.size
        if (goal < current) {
            data.remove(goal, current)
        } else for (i in current until goal) {
            data.add(LabelContent().apply { title.text.value = "Label $i" })
        }
    }
}

class LabelsDocumentDataModel : ItemViewModel<LabelsDocumentData>() {
    val title = bind(LabelsDocumentData::title)
    val units = bind<Units, SimpleObjectProperty<Units>, SimpleObjectProperty<Units>>(LabelsDocumentData::units)
    val pageWidth =
        bind<SimpleFloatProperty, Number, SimpleFloatProperty> { item?.pageSize?.width }.convertUnits(units)
    val pageHeight =
        bind<SimpleFloatProperty, Number, SimpleFloatProperty> { item?.pageSize?.height }.convertUnits(units)

    val columns = bind(LabelsDocumentData::columns)
    val rows = bind(LabelsDocumentData::rows)
    val columnCount = SimpleIntegerProperty().apply {
        bindCount(columns) { GridLineData() }
    }
    val rowCount = SimpleIntegerProperty().apply {
        bindCount(rows) { GridLineData() }
    }
    val count = bind<Number, SimpleIntegerProperty, SimpleIntegerProperty>(LabelsDocumentData::count)
    val centerH = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::centerH)
    val centerV = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::centerV)
    val offsetH = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::offsetH)
    val offsetHU = offsetH.convertUnits(units)
    val offsetV = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::offsetV)
    val offsetVU = offsetV.convertUnits(units)
    val controlLabelH = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::controlLabelH)
    val controlLabelV = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::controlLabelV)
    val labelWidth = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::labelWidth)
    val labelWidthU = labelWidth.convertUnits(units)
    val labelHeight = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::labelHeight)
    val labelHeightU = labelHeight.convertUnits(units)
    val data = bind(LabelsDocumentData::data)
    val dataByUUID = observableMapOf<String, LabelContent>()
    val drawBorder = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::drawBorder)
    val borderInside = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::borderInside)
    val drawCircle = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::drawCircle)
    val markCenter = bind<Boolean, BooleanProperty, SimpleBooleanProperty>(LabelsDocumentData::markCenter)
    val anyDrawingEnabled = drawBorder.or(drawCircle).or(markCenter)
    val borderWidth = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::borderWidth)
    val borderWidthU = borderWidth.convertUnits(units)
    val borderColor =
        bind<Color, SimpleObjectProperty<Color>, SimpleObjectProperty<Color>>(LabelsDocumentData::borderColor)
    val font = bind<Fonts, SimpleObjectProperty<Fonts>, SimpleObjectProperty<Fonts>>(LabelsDocumentData::font)
    val autoFontSizeMod  = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(LabelsDocumentData::autoFontSizeMod)
    val borderStyle =
        bind<BorderStyle, SimpleObjectProperty<BorderStyle>, SimpleObjectProperty<BorderStyle>>(LabelsDocumentData::borderStyle)

    init {
        data.onChange {
            dataByUUID.clear()
            it?.forEach {
                dataByUUID[it.uuid] = it
            }
        }
    }

    fun syncCreateData() {
        val goal = count.value.toInt()
        item.syncCreateData(goal)
    }

    var oncommit: () -> Unit = {}
    override fun onCommit() = oncommit.invoke()
}
