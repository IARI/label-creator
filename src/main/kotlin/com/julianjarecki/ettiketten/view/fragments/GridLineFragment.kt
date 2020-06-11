package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.GridLineData
import com.julianjarecki.ettiketten.app.data.GridLineDataModel
import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.app.utils.objectBindingNonNull3
import com.julianjarecki.ettiketten.styles.Styles
import com.julianjarecki.tfxserializer.utils.stringconverter
import javafx.geometry.Orientation

import tornadofx.*

class GridLineFragment : ListCellFragment<GridLineData>() {
    //val documentData: LabelsDocumentDataModel by inject()
    val model = GridLineDataModel()

    val listViewOrientation = cellProperty.objectBindingNonNull3 {
        it.listView.orientation
    }
    val widthBinding = listViewOrientation.doubleBinding {
        when (it) {
            Orientation.VERTICAL -> Styles.labelsLeftBarWidth - 12
            else -> Styles.labelCellWidth
        }
    }
    val heightBinding = listViewOrientation.doubleBinding {
        when (it) {
            Orientation.VERTICAL -> Styles.labelCellHeight
            else -> Styles.labelsTopRowHeight - 8
        }
    }


    init {
        model.bindTo(this)
    }

    override val root = pane {
        addClass(Styles.gridLine)
        prefWidthProperty().bind(widthBinding)
        prefHeightProperty().bind(heightBinding)
        vbox {
            spinner(
                0.001, 10000.0, 0.0, 0.1,
                true, enableScroll = true,
                property = model.size
            ) {
                prefWidthProperty().bind(widthBinding.minus(12))
                removeWhen(model.enableUnit)
            }
            spinner(
                0.001, 10000.0, 0.0, 0.1,
                true, enableScroll = true,
                property = model.sizeU
            ) {
                prefWidthProperty().bind(widthBinding.minus(12))
                removeWhen(model.enableUnit.not())
            }
            hbox {
                checkbox(property = model.enableUnit)
                combobox(model.units, Units.values().toList()) {
                    prefWidth = 65.0
                    prefHeight = 10.0
                    disableWhen(model.enableUnit.not())

                    addClass(Styles.noarrow)
                    //showingProperty()
                    converter = Units::short stringconverter Units.Companion::byShort
                }
            }
        }
    }
}