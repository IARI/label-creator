package com.julianjarecki.ettiketten.app.controller

import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.BlockElement
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.julianjarecki.ettiketten.app.data.*
import com.julianjarecki.ettiketten.app.utils.itext.*
import com.julianjarecki.tfxserializer.utils.replaceExtension
import tornadofx.Controller
import java.io.File
import kotlin.math.ceil
import kotlin.math.min
import com.julianjarecki.ettiketten.app.data.PageSize as MyPageSize

class PdfExportController : Controller() {
    val appSettings by inject<AppSettingsModel>()

    fun export(document: LabelsDocumentModel, data: LabelsDocumentDataModel) =
        document.labelsFile.value.replaceExtension(".pdf").apply {
            export(this, data)
        }

    val MyPageSize.pageSize get() = PageSize(width.value, height.value)

    fun export(target: File, data: LabelsDocumentDataModel) {
        val eData = data.eData

        target.writePdf {
            pdfDocument {
                info {
                    title = data.title.value
                    author = appSettings.pdfAuthor.value
                    creator = "Label Creator Application (by Julian Jarecki)"

                    setMoreInfo("test", "Test")
                }

                //catalog.put(PdfName.Lang,)
                /*
                trailer.apply {
                }
                */
                /*
                document(PageSize.A4) {
                    font = Fonts.HELVETICA_BOLD

                    list {
                        symbolIndent = 12.0f
                        setListSymbol("\u2022")
                        font = Fonts.COURIER
                        item("Hello World 4")
                    }

                    table(5f, 50f, 120f) {
                        headerCell { paragraph("h1") }
                        headerCell { paragraph("h2") }
                        headerCell { paragraph("h3") }
                        for (i in 0 until 10) {
                            cell { paragraph("c$i") }
                        }
                    }

                    image("E:\\Bilder\\micha.jpg")

                    close()
                }
                */

                canvas(eData.ps) {
                    export(data, eData)
                }
                close()
            }
        }
    }

    private data class EffectiveLabelsData(
        val ps: PageSize,
        val columns: Int,
        val offsetH: Float,
        val offsetV: Float,
        val colWidth: Array<Float>,
        val rowHeight: Array<Float>
    ) {
        val colOffset = colWidth.scan(0f) { acc, fl -> acc + fl }
        val rowOffset = rowHeight.scan(0f) { acc, fl -> acc + fl }
    }

    private val LabelsDocumentDataModel.eData: EffectiveLabelsData
        get() {
            val ps = item.pageSize.pageSize
            var offseth = offsetH.value.toFloat()
            var offsetv = offsetV.value.toFloat()
            val lheight = labelHeight.value.toFloat()
            val lwidth = labelWidth.value.toFloat()
            val widthes: Array<Float>
            val heights: Array<Float>
            var cols = columns.value.size
            //var rws = rows.value.size
            val rowsByCount = ceil(count.value.toDouble() / cols).toInt()

            if (controlLabelH.value) {
                if (cols * lwidth > ps.width) cols = (ps.width / lwidth).toInt()
                if (centerH.value) offseth = (ps.width - lwidth * cols) / 2
                widthes = Array(cols) { lwidth }
            } else {
                val availableWidth = ps.width - 2 * offseth
                widthes = computeGridLineSizes(columns.value, availableWidth)
            }
            if (controlLabelV.value) {
                val maxRows = (ps.height / lheight).toInt()
                val rws = min(rowsByCount, maxRows)
                if (centerV.value) offsetv = (ps.height - lheight * rws) / 2
                heights = Array(rowsByCount) { lheight }
            } else {
                val availableHeight = ps.height - 2 * offsetv
                heights = computeGridLineSizes(rows.value, availableHeight)
            }

            return EffectiveLabelsData(ps, cols, offseth, offsetv, widthes, heights)
        }

    private fun computeGridLineSizes(lines: List<GridLineData>, availableSpace: Float): Array<Float> {
        var fixedSpace = 0f
        var totalFractional = 0.0
        lines.forEach {
            if (it.enableUnit.value) {
                fixedSpace += it.size.value.toFloat()
            } else {
                totalFractional += it.size.value
            }
        }
        val availableRelativeSpace = availableSpace - fixedSpace
        return Array(lines.size) {
            lines[it].run {
                if (enableUnit.value)
                    size.value.toFloat()
                else
                    availableRelativeSpace * (size.value / totalFractional).toFloat()
            }
        }
    }

    private fun PdfCanvas.export(data: LabelsDocumentDataModel, eData: EffectiveLabelsData) {
        lineJoinStyle = LineJoinStyles.ROUND
        //setFontAndSize(data.font.value.PdfFont, 12f)

        //concatMatrix(1.0, .0, .0, 1.0, ps.width / 2.0, ps.height / 2.0)
        concatMatrix(1.0, .0, .0, 1.0, .0, eData.ps.height.toDouble())
        val border = data.borderStyle.value.construct(data.borderColor.value, data.borderWidth.value)

        data.data.value.forEachIndexed { index, labelContent ->
            val linkedLabel = data.dataByUUID.get(labelContent.linkedTo.value) ?: labelContent
            val col = index.rem(eData.columns)
            val row = index.div(eData.columns)
            val xOffset = eData.colOffset[col] + eData.offsetH
            val yOffset = eData.rowOffset.getOrElse(row) { eData.rowOffset.last() } + eData.offsetV
            val lWidth = eData.colWidth[col]
            val lHeight = eData.rowHeight.getOrElse(row) { eData.rowHeight.last() }
            val enableSubtitle = linkedLabel.enableSubTitle.value

            rectCanvas(xOffset, -(yOffset + lHeight), lWidth, lHeight, {
                if (data.drawBorder.value) {
                    setLineWidth(border.width)
                    setStrokeColor(data.borderColor.value.iText)
                    val bwhalf: Float = if (data.borderInside.value) 0f else border.width / 2
                    val xLeft = left + bwhalf
                    val yBottom = bottom + bwhalf
                    val xRight = right - bwhalf
                    val yTop = top - bwhalf
                    border.apply {
                        draw(
                            this@export, xLeft, yTop, xRight, yTop,
                            Border.Side.TOP, border.width, border.width
                        )
                        draw(
                            this@export, xRight, yTop, xRight, yBottom,
                            Border.Side.RIGHT, border.width, border.width
                        )
                        draw(
                            this@export, xRight, yBottom, xLeft, yBottom,
                            Border.Side.BOTTOM, border.width, border.width
                        )
                        draw(
                            this@export, xLeft, yBottom, xLeft, yTop,
                            Border.Side.LEFT, border.width, border.width
                        )
                    }
                }
            }) {
                div {
                    setVerticalAlignment(VerticalAlignment.MIDDLE)
                    height = lHeight.point
                    val f = data.font.value.PdfFont
                    font = f

                    val lHeightTitlePart = if (enableSubtitle) .6f else 1f
                    renderTextContent(f, linkedLabel.title, lWidth, lHeight * lHeightTitlePart)

                    if (enableSubtitle) {
                        renderTextContent(f, linkedLabel.subTitle, lWidth, lHeight * (1 - lHeightTitlePart))
                    }
                }
            }
        }
    }

    fun BlockElement<*>.renderTextContent(
        font: PdfFont,
        textContent: TextContent,
        lwidth: Float,
        lheight: Float
    ) {
        val fontSize = if (textContent.autoSize.value) {
            font
                //.getBiggestFontSize(textContent.text.value, lwidth * .85f, lheight * 0.8f)
                .getBiggestFontSize(
                    textContent.text.value,
                    lwidth * .85f,
                    appSettings.labelFontheightFraction.floatValue() * lheight
                )
        } else textContent.size.value.toFloat()

        renderTextContent(fontSize, textContent, textContent.text.value)
    }

    fun BlockElement<*>.renderTextContent(
        fontSize: Float,
        textContent: TextContent,
        text: String
    ) {
        paragraph(text) {
            setTextAlignment(TextAlignment.CENTER)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            //setSpacingRatio()
            setMultipliedLeading(appSettings.multipliedLeading.floatValue())
            //setBackgroundColor(DeviceRgb.GREEN)
            //setBorder(DottedBorder(1f))
            fontColor = textContent.color.value
            setFontSize(fontSize)
        }
    }


}