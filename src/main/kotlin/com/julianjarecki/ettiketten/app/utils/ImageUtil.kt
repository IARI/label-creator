package com.julianjarecki.ettiketten.app.utils

import de.jensd.fx.glyphs.GlyphIcons
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import tornadofx.*
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataFormatImpl
import javax.imageio.metadata.IIOMetadataNode

val RenderedImage.base64string: String
    get() = ByteArrayOutputStream().let {
        ImageIO.write(this, "jpg", it)
        return String(Base64.getEncoder().encode(it.toByteArray()))
        //return Base64.getEncoder().encode(it.toByteArray()).stringFromUtf8
    }

val Image.bufferedImage
    get() = BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_RGB)
            .apply {
                for (x in 0..width - 1)
                    for (y in 0..height - 1)
                        setRGB(x, y, pixelReader.getColor(x, y).toAWT.rgb)
            }

val BufferedImage.fxImage get() = SwingFXUtils.toFXImage(this, null)

val Double.toRGBInt get() = (this * 255).toInt()
val javafx.scene.paint.Color.toAWT get() = java.awt.Color(red.toRGBInt, green.toRGBInt, blue.toRGBInt)

val Int.hex get() = String.format("%x", this)
val Color.hex get() = "#${red.toRGBInt.hex}${green.toRGBInt.hex}${blue.toRGBInt.hex}"
val Color.html get() = "rgba(${red.toRGBInt},${green.toRGBInt},${blue.toRGBInt},$opacity)"

val File.readImage: BufferedImage?
    get() = if (extension == "svg") try {
        null
    } catch (e: Throwable) {
        null
    } else if (canRead())
        ImageIO.read(this)
    else null

//val File.readSvgImage: Image get() = inputStream().readSvgImage
//val String.readSvgImage: Image get() = byteInputStream().readSvgImage

/*
val InputStream.readSvgImage: Image
    get() {
        val loader = SvgLoader()
        //loader.setAddViewboxRect(true)
        //        val scgDoc = loader.loadSvgDocument(inputStream())
        //        scgDoc.dim
        //val loadedSvg = Group().let { loader. }
        val loadedSvg = loader.loadSvg(this)
        //loadedSvg.scaleX = 0.8
        //loadedSvg.scaleY = 0.8
        val scene = Scene(loadedSvg)
        val bounds = loadedSvg.boundsInLocal
        val img = WritableImage(bounds.width.toInt(), bounds.height.toInt())
        return scene.snapshot(img)
    }
*/

/*
val File.readSvgImage
    get() : BufferedImage? {
        val imagePointer = arrayOfNulls<BufferedImage>(1)
        val css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}"
        val cssFile = File.createTempFile("batik-default-override-", ".css")
        FileUtils.writeStringToFile(cssFile, css)

        val transcoderHints = TranscodingHints()
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, java.lang.Boolean.FALSE)
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation())
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI)
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg")
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString())

        try {

            val input = TranscoderInput(FileInputStream(this))

            val t = object : ImageTranscoder() {
                override fun createImage(w: Int, h: Int): BufferedImage =
                        BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

                override fun writeImage(image: BufferedImage, out: TranscoderOutput) {
                    imagePointer[0] = image
                }
            }
            t.setTranscodingHints(transcoderHints)
            t.transcode(input, null)
        } catch (ex: TranscoderException) {
            // Requires Java 6
            ex.printStackTrace()
            val msg = "Couldn't convert $this"
            //alert(Alert.AlertType.ERROR, msg)
            throw IOException(msg)
        } finally {
            cssFile.delete()
        }

        return imagePointer[0]
    }
*/

/*
val File.readFXImage: Image?
    get() = if (extension == "svg") try {
        readSvgImage
    } catch (e: Throwable) {
        null
    } else readImage?.fxImage
*/

enum class Format(val ext: String) {
    JPG("jpg"),
    GIF("gif"),
    PNG("png"),
    SVG("svg"),
    ;

    val dotExt get() = ".$ext"
}

fun BufferedImage.writeTo(file: File, format: Format) {
    ImageIO.write(this, format.ext, file)
}

fun Image.writeTo(file: File, format: Format) {
    bufferedImage.writeTo(file, format)
}

data class ImageWithMeta(val image: BufferedImage, val meta: IIOMetadata)

val NodeList.sequence
    get() = sequence<Node> {
        for (i in 0..length) {
            yield(item(i))
        }
    }

operator fun IIOMetadataNode.get(attribute: String) = this.getAttribute(attribute)

enum class Keys {
    TextEntry,
    keyword,
    value
}

val IIOMetadata.treeNode get() = (getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName) as IIOMetadataNode)

operator fun IIOMetadata.get(key: String) = treeNode.getElementsByTagName(Keys.TextEntry.name).sequence.find {
    (it as IIOMetadataNode)[Keys.keyword.name].equals(key)
}

val File.imageWithMeta
    get() = ImageIO.createImageInputStream(this).let { stream ->
        ImageIO.getImageReaders(stream).next().run {
            setInput(stream, true, false)
            ImageWithMeta(read(0, defaultReadParam), getImageMetadata(0))
        }
    }


/*fun Component.selectImages(dir: File,
                           filter: (File) -> Boolean = { true },
                           handler: ImageSelector.(ImageFile) -> Unit) = dir.letif({ it.isDirectory }) {
    it.listFiles { f -> f.isFile && f.extension.toLowerCase() in imageExtensions && f.let(filter) }
}?.map { imageFile(it) }?.filterNotNull()?.letif({ it.isNotEmpty() }) {
    find<ImageSelector>(mapOf("items" to it.toList().asObservable(), "okAction" to handler)).openModal()
}*/


inline val <T : GlyphIcons> T.view
    get() = when (this) {
        is MaterialDesignIcon -> MaterialDesignIconView(this)
        is FontAwesomeIcon -> FontAwesomeIconView(this)
        is MaterialIcon -> MaterialIconView(this)
        // is EmojiOne -> EmojiOneView(this)
        // is Icons525 -> Icons525View(this)
        // is WeatherIcon -> WeatherIconView(this)
        // is OctIcon -> OctIconView(this)
        else -> throw TypeNotPresentException("", null);
    }


val ObservableValue<GlyphIcons?>.view get() = Group().apply {
    //val content = SimpleOb
    onChange {
        it?.let {
            replaceChildren(it.view)
        }
    }
}