package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.LabelContent
import com.julianjarecki.ettiketten.app.data.LabelContentModel
import com.julianjarecki.ettiketten.app.data.LabelsDocumentDataModel
import com.julianjarecki.ettiketten.app.utils.view
import com.julianjarecki.ettiketten.styles.Styles
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.geometry.Pos
import tornadofx.*

class LabelContentFragment : DataGridCellFragment<LabelContent>() {
    val documentData: LabelsDocumentDataModel by inject()
    val labelContent = LabelContentModel()
    val linked = LabelContentModel()

    init {
        labelContent.bindTo(this)

        val linkedBinding =
            labelContent.linkedTo.objectBinding<String, LabelContent>(labelContent.linkedTo, itemProperty) { uuid ->
                documentData.dataByUUID.get(uuid) ?: item
            }

        linked.itemProperty.bind(linkedBinding)
    }

    override val root = stackpane {
        hbox {
            alignment = Pos.BOTTOM_CENTER
            visibleWhen(labelContent.linkedTo.isNotEmpty)
            this += MaterialIcon.LINK.view.apply {
                setSize("42px")
                setStyleClass(Styles.backgroundNumber.name)
            }
            label(labelContent.linkedTo) {
                addClass(Styles.backgroundNumber)
                isWrapText = true
            }
        }
        vbox {
            //visibleWhen(labelContent.linkedTo.isEmpty)
            textContentFragment(linked.title)

            checkbox("Subtitle", linked.enableSubTitle)

            textContentFragment(linked.subTitle) {
                //root.enableWhen(content.enableSubTitle)
                root.hiddenWhen(!linked.enableSubTitle)
            }
        }
    }
}