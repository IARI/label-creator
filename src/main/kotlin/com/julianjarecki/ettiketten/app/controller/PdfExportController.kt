package com.julianjarecki.ettiketten.app.controller

import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.BlockElement
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.julianjarecki.ettiketten.app.data.AppSettingsModel
import com.julianjarecki.ettiketten.app.data.LabelsDocumentDataModel
import com.julianjarecki.ettiketten.app.data.LabelsDocumentModel
import com.julianjarecki.ettiketten.app.data.TextContent
import com.julianjarecki.ettiketten.app.utils.itext.*
import com.julianjarecki.tfxserializer.utils.replaceExtension
import tornadofx.Controller
import java.io.File
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

                    paragraph("Hello World")
                    paragraph("${data.rows.value} x ${data.columns.value}") {
                    }

                    list {
                        symbolIndent = 12.0f
                        setListSymbol("\u2022")
                        font = Fonts.COURIER

                        item("Hello World")
                        item("Hello World 2")
                        item("Hello World 3")
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
                    export(this@pdfDocument, data, eData)
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
        val lwidth: Float,
        val lheight: Float
    )

    private val LabelsDocumentDataModel.eData: EffectiveLabelsData
        get() {
            val ps = item.pageSize.pageSize
            var offseth = offsetH.value.toFloat()
            var offsetv = offsetV.value.toFloat()
            var lheight = labelHeight.value.toFloat()
            var lwidth = labelWidth.value.toFloat()
            var cols = columns.value

            if (controlLabelH.value) {
                if (cols * lwidth > ps.width) cols = (ps.width / lwidth).toInt()
                if (centerH.value) offseth = (ps.width - lwidth * cols) / 2
            } else {
                lwidth = (ps.width - 2 * offseth) / columns.value
            }
            if (controlLabelV.value) {
                val maxRows = (ps.height / lheight).toInt()
                val rows = min(Math.ceil(count.value.toDouble() / cols).toInt(), maxRows)
                if (centerV.value) offsetv = (ps.height - lheight * rows) / 2
            } else {
                lheight = (ps.height - 2 * offsetv) / rows.value
            }

            return EffectiveLabelsData(ps, cols, offseth, offsetv, lwidth, lheight)
        }

    private fun PdfCanvas.export(pdfDoc: PdfDocument, data: LabelsDocumentDataModel, eData: EffectiveLabelsData) {
        lineJoinStyle = LineJoinStyles.ROUND
        //setFontAndSize(data.font.value.PdfFont, 12f)

        //concatMatrix(1.0, .0, .0, 1.0, ps.width / 2.0, ps.height / 2.0)
        concatMatrix(1.0, .0, .0, 1.0, .0, eData.ps.height.toDouble())
        val border = data.borderStyle.value.construct(data.borderColor.value, data.borderWidth.value)

        data.data.value.forEachIndexed { index, labelContent ->
            val linkedLabel = data.dataByUUID.get(labelContent.linkedTo.value) ?: labelContent
            val xOffset = eData.lwidth * index.rem(eData.columns) + eData.offsetH
            val yOffset = eData.lheight * index.div(eData.columns) + eData.offsetV

            rectCanvas(pdfDoc, xOffset, -(yOffset + eData.lheight), eData.lwidth, eData.lheight, {
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
                    height = eData.lheight.point
                    val f = data.font.value.PdfFont
                    font = f

                    val lHeightTitlePart = if (linkedLabel.enableSubTitle.value) .6f else 1f
                    renderTextContent(f, linkedLabel.title, eData.lwidth, eData.lheight * lHeightTitlePart)

                    if (linkedLabel.enableSubTitle.value) {
                        renderTextContent(f, linkedLabel.subTitle, eData.lwidth, eData.lheight * (1 - lHeightTitlePart))
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
    ) = paragraph(textContent.text.value) {
        setTextAlignment(TextAlignment.CENTER)
        setVerticalAlignment(VerticalAlignment.MIDDLE)
        //setSpacingRatio()
        setMultipliedLeading(1.05f)
        //setBackgroundColor(DeviceRgb.GREEN)
        //setBorder(DottedBorder(1f))
        fontColor = textContent.color.value

        if (textContent.autoSize.value) {
            font
                //.getBiggestFontSize(textContent.text.value, lwidth * .85f, lheight * 0.8f)
                .getBiggestFontSize(textContent.text.value, lwidth * .85f, lheight)
                .let(::setFontSize)
        } else {
            setFontSize(textContent.size.value.toFloat())
        }
    }


}