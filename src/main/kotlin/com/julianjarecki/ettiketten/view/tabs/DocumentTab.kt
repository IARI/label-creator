package com.julianjarecki.ettiketten.view.tabs

import com.julianjarecki.ettiketten.app.controller.appSettings
import com.julianjarecki.ettiketten.app.controller.exportPdf
import com.julianjarecki.ettiketten.app.data.LabelContent
import com.julianjarecki.ettiketten.app.data.LabelsDocumentDataModel
import com.julianjarecki.ettiketten.app.data.LabelsDocumentModel
import com.julianjarecki.ettiketten.app.utils.*
import com.julianjarecki.ettiketten.app.utils.plus
import com.julianjarecki.ettiketten.app.utils.itext.BorderStyle
import com.julianjarecki.ettiketten.app.utils.itext.Fonts
import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.styles.Styles
import com.julianjarecki.ettiketten.view.base.AppTab
import com.julianjarecki.ettiketten.view.fragments.GridLineFragment
import com.julianjarecki.ettiketten.view.fragments.LabelContentFragment
import com.julianjarecki.tfxserializer.utils.openWithDefaultApp
import com.julianjarecki.tfxserializer.utils.starIfDirty
import com.julianjarecki.tfxserializer.utils.swapValueWith
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.geometry.Orientation
import javafx.scene.control.SelectionMode
import javafx.scene.control.ToggleGroup
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class DocumentTab : AppTab("Document", MaterialDesignIcon.LABEL_OUTLINE.view) {
    val appPreferences by appSettings()
    val pdf by exportPdf()
    val Document: LabelsDocumentModel by inject()
    val data: LabelsDocumentDataModel by inject()
    val tgroupH = ToggleGroup()
    val tgroupV = ToggleGroup()
    private val paddingLeftBinding = data.controlLabelV.doubleBinding {
        log.info("Doc controlLabelV: $it")
        if (it ?: true) .0 else Styles.labelsLeftBarWidth
    }


    val selectedLabels = observableListOf<LabelContent>()

    override fun onDock() {
        titleProperty.bind(data.title + data.starIfDirty)

        data.oncommit = Document::save

        data.count.onChange {
            log.info("$it")
            data.syncCreateData()
        }

        tgroupH.bind(data.controlLabelH)
        tgroupV.bind(data.controlLabelV)
    }

    override val root = hbox {
        vgrow = Priority.ALWAYS
        drawer(multiselect = true) {
            item("Document info", icon = MaterialDesignIcon.FILE_DOCUMENT.view, expanded = true) {
                form {
                    fieldset {
                        field("Title") {
                            textfield(data.title)
                        }
                    }
                }
            }
            item("Dimensions", icon = MaterialDesignIcon.RULER.view, expanded = true) {
                form {
                    fieldset {
                        field("Units") {
                            combobox(data.units, Units.values().toList())
                        }
                        field("Page size") {
                            textfield(data.pageWidth)
                            textfield(data.pageHeight)
                            //pageSize
                            button("", MaterialDesignIcon.SCREEN_ROTATION.view) {
                                action {
                                    data.pageWidth.swapValueWith(data.pageHeight)
                                }
                            }
                        }
                        field("Count") {
                            spinner(1, 10000, 0, 1, true, enableScroll = true, property = data.count)
                        }
                        hbox {
                            vbox {
                                field {
                                    checkbox("center Horizontally", data.centerH)
                                }
                                field("Offset Horizontal") {
                                    spinner(
                                        .5, 100.0, amountToStepBy = .5, editable = true,
                                        enableScroll = true, property = data.offsetHU
                                    ) {
                                        prefWidth = 100.0
                                    }
                                }
                                field {
                                    togglebutton("Width", tgroupH, value = true) {
                                        prefWidth = 80.0
                                    }
                                    spinner(
                                        .5, 10000.0, amountToStepBy = .5, editable = true,
                                        enableScroll = true, property = data.labelWidthU
                                    ) {
                                        prefWidth = 100.0
                                        enableWhen(data.controlLabelH)
                                    }
                                }
                                field {
                                    togglebutton("Columns", tgroupH, value = false) {
                                        prefWidth = 80.0
                                    }
                                    spinner(
                                        1, 100, 0, 1, true,
                                        enableScroll = true, property = data.columnCount
                                    ) {
                                        prefWidth = 100.0
                                        disableWhen(data.controlLabelH)
                                    }
                                }
                            }
                            spacer {
                                minWidth = 4.0
                            }
                            vbox {
                                field {
                                    checkbox("center Vertically", data.centerV)
                                }
                                field("Offset Vertical") {
                                    spinner(
                                        .5, 100.0, amountToStepBy = .5, editable = true,
                                        enableScroll = true, property = data.offsetVU
                                    ) {
                                        prefWidth = 100.0
                                    }
                                }
                                field {
                                    togglebutton("Height", tgroupV, value = true) {
                                        prefWidth = 80.0
                                    }
                                    spinner(
                                        .5, 10000.0, amountToStepBy = .5, editable = true,
                                        enableScroll = true, property = data.labelHeightU
                                    ) {
                                        prefWidth = 100.0
                                        enableWhen(data.controlLabelV)
                                    }
                                }
                                field {
                                    togglebutton("Rows", tgroupV, value = false) {
                                        prefWidth = 80.0
                                    }
                                    spinner(
                                        1, 100, 0, 1, true,
                                        enableScroll = true, property = data.rowCount
                                    ) {
                                        prefWidth = 100.0
                                        disableWhen(data.controlLabelV)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item("Drawing", icon = MaterialDesignIcon.DRAWING.view, expanded = data.anyDrawingEnabled.value) {
                form {
                    fieldset {
                        field("Font") {
                            combobox(data.font, Fonts.values().toList())
                        }
                        field("Fontsize mod") {
                            spinner(
                                -100.0, 100.0, amountToStepBy = .1, editable = true,
                                property = data.autoFontSizeMod, enableScroll = true
                            ) {
                                prefWidth = 100.0
                            }
                        }
                        hbox {
                            togglebutton("Border") {
                                selectedProperty().bindBidirectional(data.drawBorder)
                            }
                            togglebutton("Circle") {
                                selectedProperty().bindBidirectional(data.drawCircle)
                            }
                            togglebutton("Mark Center") {
                                selectedProperty().bindBidirectional(data.markCenter)
                            }
                        }
                        vbox {
                            enableWhen(data.anyDrawingEnabled)
                            field {
                                colorpicker(data.borderColor) {
                                    //prefWidth = 20.0
                                }
                                combobox(data.borderStyle, BorderStyle.values().toList()) {

                                }
                            }
                            field {
                                checkbox("inside", data.borderInside) {
                                }
                                //spinner(5.0, 100.0, amountToStepBy = 0.25, editable =  true, property = textContent.size, enableScroll = true) {
                                spinner(
                                    .05, 100.0, amountToStepBy = .1, editable = true,
                                    property = data.borderWidthU, enableScroll = true
                                ) {
                                    prefWidth = 100.0
                                }
                            }
                        }
                    }
                }
            }
        }
        borderpane {
            hgrow = Priority.ALWAYS
            top {
                listview(data.columns) {
                    addClass(Styles.gridLineList)
                    paddingLeftProperty.bind(paddingLeftBinding)
                    orientation = Orientation.HORIZONTAL
                    prefHeight = Styles.labelsTopRowHeight
                    cellFragment(GridLineFragment::class)
                }
            }
            left {
                listview(data.rows) {
                    addClass(Styles.gridLineList)
                    prefWidth = Styles.labelsLeftBarWidth
                    orientation = Orientation.VERTICAL
                    cellFragment(GridLineFragment::class)
                }
            }
            center {
                datagrid<LabelContent>(scope = this@DocumentTab.scope) {
                    hgrow = Priority.ALWAYS
                    maxCellsInRowProperty.bind(data.columns.select { it.sizeProperty })
                    cellWidth = Styles.labelCellWidth
                    cellHeight = Styles.labelCellHeight
                    horizontalCellSpacing = .0
                    verticalCellSpacing = .0
                    //cellHeightProperty.bind()

                    selectionModel.selectionMode = SelectionMode.MULTIPLE
                    selectedLabels.bind(selectionModel.selectedItems, { it })

                    data.columns.onChange {
                        DataGridSkin::class.java.getDeclaredMethod("updateItems").apply {
                            isAccessible = true
                            invoke(this@datagrid.skin)
                        }
                    }

                    contextmenu {
                        item("link", graphic = FontAwesomeIcon.LINK.view).action(::linkData)
                        item("unlink", graphic = FontAwesomeIcon.UNLINK.view).action(::unlinkData)
                    }

                    itemsProperty.bind(data.data)

                    cellFragment<LabelContentFragment>()
                }
            }

            addClass(Styles.gridBackground)
            top.removeWhen(data.controlLabelH)
            left.apply {
                removeWhen(data.controlLabelV)
                prefWidth = Styles.labelsLeftBarWidth
            }
        }
    }

    override fun keyboardEvent(event: KeyEvent) {
        super.keyboardEvent(event)

        if (event.isShortcutDown) {
            when (event.code) {
                KeyCode.L -> toggleLinkData()
                else -> Unit
            }
        }
    }

    private fun toggleLinkData() {
        if (selectedLabels.any { it.linkedTo.value.isEmpty() })
            linkData() else unlinkData()
    }

    private fun linkData() {
        selectedLabels.firstOrNull()?.uuid?.let { uuid ->
            selectedLabels.forEach {
                it.linkedTo.value = uuid
            }
        }
    }

    private fun unlinkData() {
        selectedLabels.forEach {
            it.linkedTo.value = ""
        }
    }

    override fun hasSameScrope(newScope: Scope) =
        Document.labelsFile.value.absolutePath == find<LabelsDocumentModel>(newScope).labelsFile.value.absolutePath

    override fun saveData() {
        data.commit()
    }

    override fun export() {
        pdf.export(Document, data).apply {
            if (appPreferences.openDocumentAfterExport.value) openWithDefaultApp()
            //openInExplorer()
        }
    }
}