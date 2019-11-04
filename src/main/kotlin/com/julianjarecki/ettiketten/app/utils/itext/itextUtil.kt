package com.julianjarecki.ettiketten.app.utils.itext

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.ElementPropertyContainer
import com.itextpdf.layout.RootElement
import com.itextpdf.layout.borders.*
import com.itextpdf.layout.element.*
import com.itextpdf.layout.element.List
import com.itextpdf.layout.property.Property
import com.itextpdf.layout.property.UnitValue
import javafx.scene.paint.Color
import java.io.File
import kotlin.math.min

const val userUnitPerInch = 72
const val mmPerInch = 25.4

enum class Units(val points: Double, short: String) {
    Point(1.0, "pt"),
    Inch(userUnitPerInch.toDouble(), "in"),
    Millimeter(userUnitPerInch / mmPerInch, "mm"),
    Centimeter(10 * userUnitPerInch / mmPerInch, "cm")
    ;
}

//import com.julianjarecki.ettiketten.app.data.PageSize as myPageSize
//val myPageSize.itPagesize get() = PageSize(width.value.toFloat(), height.value.toFloat())

inline fun File.writePdf(properties: WriterProperties? = null, op: PdfWriter.() -> Unit) =
    if (properties != null) {
        PdfWriter(absolutePath, properties)
    } else {
        PdfWriter(this)
    }.apply(op)

inline fun PdfWriter.pdfDocument(properties: DocumentProperties? = null, op: PdfDocument.() -> Unit) =
    if (properties != null) {
        PdfDocument(this, properties)
    } else {
        PdfDocument(this)
    }.apply(op)

inline fun PdfDocument.info(op: PdfDocumentInfo.() -> Unit) = documentInfo.apply(op)

inline fun PdfDocument.document(
    pageSize: PageSize = PageSize.A4,
    immidiateFlush: Boolean = false,
    op: Document.() -> Unit
) = Document(this, pageSize, immidiateFlush).apply(op)


inline fun PdfDocument.page(pageSize: PageSize = PageSize.A4, op: PdfPage.() -> Unit) = addNewPage(pageSize).apply(op)
inline fun PdfPage.canvas(op: PdfCanvas.() -> Unit) = PdfCanvas(this).apply(op)
inline fun PdfDocument.canvas(pageSize: PageSize = PageSize.A4, op: PdfCanvas.() -> Unit) =
    page(pageSize) { canvas(op) }

enum class LineJoinStyles(val ljVal: Int) {
    ROUND(PdfCanvasConstants.LineJoinStyle.ROUND),
    BEVEL(PdfCanvasConstants.LineJoinStyle.BEVEL),
    MITER(PdfCanvasConstants.LineJoinStyle.MITER)
    ;
}

inline var PdfCanvas.lineJoinStyle: LineJoinStyles
    get() = throw NotImplementedError()
    set(value) {
        setLineJoinStyle(value.ljVal)
    }

inline fun PdfCanvas.text(op: PdfCanvas.() -> Unit) = apply {
    beginText()
    apply(op)
    endText()
}

inline fun PdfCanvas.rectCanvas(
    pdf: PdfDocument,
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    opr: Rectangle.() -> Unit = {},
    op: Canvas.() -> Unit = {}
) = Rectangle(x, y, w, h).apply(opr).run {
    Canvas(this@rectCanvas, pdf, this).apply(op).close()
}

val Float.point get() = UnitValue(UnitValue.POINT, this)
val Float.percent get() = UnitValue(UnitValue.PERCENT, this)
inline fun RootElement<*>.div(
    w: UnitValue = 100f.percent,
    h: UnitValue = 100f.percent,
    op: Div.() -> Unit = {}
) = add(Div().apply {
    width = w
    height = h
}.apply(op))

inline fun RootElement<*>.paragraph(text: String, op: Paragraph.() -> Unit = {}) = add(Paragraph(Text(text)).apply(op))
inline fun <T : BlockElement<*>> T.paragraph(text: String, op: Paragraph.() -> Unit = {}) =
    Paragraph(Text(text)).apply(op).let { p ->
        when (this) {
            is Cell -> add(p)
            is Div -> add(p)
            else -> throw NotImplementedError("no implementation for ${javaClass.name}")
        }
    }

fun RootElement<*>.image(path: String) = add(Image(ImageDataFactory.create(path)))
fun Paragraph.image(path: String) = add(Image(ImageDataFactory.create(path)))
fun Paragraph.image(file: File) = image(file.absolutePath)


@JvmName("tableVararg")
inline fun RootElement<*>.table(vararg colWidths: Float, op: Table.() -> Unit = {}) = add(Table(colWidths).apply(op))

inline fun RootElement<*>.table(colWidths: FloatArray, op: Table.() -> Unit = {}) = add(Table(colWidths).apply(op))
inline fun Table.cell(op: Cell.() -> Unit) = addCell(Cell().apply(op))
inline fun Table.headerCell(op: Cell.() -> Unit) = addHeaderCell(Cell().apply(op))


inline fun Document.list(op: List.() -> Unit = {}) = add(List().apply(op))
inline fun List.item(text: String? = null, op: ListItem.() -> Unit = {}) =
    add((if (text != null) ListItem(text) else ListItem()).apply(op))

var ElementPropertyContainer<*>.font: PdfFont
    get() = getProperty(Property.FONT)
    set(value) {
        setFont(value)
    }


enum class Fonts(val fontName: String) {
    COURIER(StandardFonts.COURIER),
    COURIER_BOLD(StandardFonts.COURIER_BOLD),
    COURIER_OBLIQUE(StandardFonts.COURIER_OBLIQUE),
    COURIER_BOLDOBLIQUE(StandardFonts.COURIER_BOLDOBLIQUE),
    HELVETICA(StandardFonts.HELVETICA),
    HELVETICA_BOLD(StandardFonts.HELVETICA_BOLD),
    HELVETICA_OBLIQUE(StandardFonts.HELVETICA_OBLIQUE),
    HELVETICA_BOLDOBLIQUE(StandardFonts.HELVETICA_BOLDOBLIQUE),
    SYMBOL(StandardFonts.SYMBOL),
    TIMES_ROMAN(StandardFonts.TIMES_ROMAN),
    TIMES_BOLD(StandardFonts.TIMES_BOLD),
    TIMES_ITALIC(StandardFonts.TIMES_ITALIC),
    TIMES_BOLDITALIC(StandardFonts.TIMES_BOLDITALIC),
    ZAPFDINGBATS(StandardFonts.ZAPFDINGBATS),
    ;

    val PdfFont get() = PdfFontFactory.createFont(fontName)
}


fun PdfFont.getBiggestFontSize(text: String, availableWidth: Float, availableHeight: Float): Float {
    val fontSizeWidth = (availableWidth / (getContentWidth(PdfString(text)) * getFontMatrix()[0]))
    //val fontSizeHeight = (100f * availableHeight / getHeight(text, 100f))
    val fontSizeHeight = availableHeight
    return min(fontSizeWidth.toFloat(), fontSizeHeight)
}


fun PdfFont.getHeight(text: String, fontSize: Float = 12f) = getAscent(text, fontSize) - getDescent(text, fontSize)

//font.getContentWidth(new PdfString(text)) * font.getFontMatrix()[0]* size

enum class BorderStyle(val c: (color: DeviceRgb, width: Float, opacity: Float) -> Border) {
    Solid(::SolidBorder),
    DoubleB(::DoubleBorder),
    //Outset(::OutsetBorder),
    //Inset(::InsetBorder),
    //Groove(::GrooveBorder),
    //Ridge(::RidgeBorder),
    Dotted(::DottedBorder),
    Dashed(::DashedBorder),
    RoundDots(::RoundDotsBorder),
    ;

    fun construct(color: Color, width: Double): Border = c(color.iText, width.toFloat(), color.opacity.toFloat())
}