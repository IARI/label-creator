package com.julianjarecki.ettiketten.app.utils

import com.julianjarecki.ettiketten.app.fxproperties.FileProperty
import com.julianjarecki.ettiketten.app.fxproperties.jfxJsonSerializer
import com.julianjarecki.ettiketten.app.fxproperties.jfxJsonSerializerNonstrict
import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.stage.FileChooser
import kotlinx.serialization.DeserializationStrategy
import tornadofx.*
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance

inline val String.toPath get() = Paths.get(this)
inline val String.toFile get() = File(this)

val userHomeDir = System.getProperty("user.home").toFile
//val userDocDir =  Shell32Util.getFolderPath(ShlObj.CSIDL_PERSONAL)
//System.getProperty("user.home").toFile

fun <T> ObservableValue<T>.resolve(child: ObservableValue<String>) = stringBinding(child) {
    it?.run {
        when (this) {
            is String -> toPath
            is File -> toPath()
            else -> throw UnsupportedOperationException()
        }.resolve(child.value ?: "pathNameIsNull").toFile().absolutePath
    }
}

fun ObservableValue<out File?>.resolveF(child: ObservableValue<String>) = objectBinding(child) {
    it?.run {
        toPath().resolve(child.value ?: "pathNameIsNull").toFile()
    }
}

fun ObservableValue<String>.resolve(vararg pathElements: ObservableValue<String>) = pathElements.fold(this) { parent, child -> parent.resolve<String>(child) }
fun ObservableValue<out File?>.resolveF(vararg pathElements: ObservableValue<String>) = pathElements.fold(this) { parent, child -> parent.resolveF(child) }
fun File.resolveR(vararg pathElements: String) = pathElements.fold(this) { parent, child -> parent.resolve(child) }


/**
 * @param callback will only be called, when deserialization worked.
 */
inline fun <reified T : Any> File.loadSettings(name: String,
                                               serializer: DeserializationStrategy<T>,
                                               strict: Boolean = false,
                                               default: () -> T = T::class::createInstance,
                                               log: Logger = Logger.getGlobal(),
                                               callback: T.() -> Unit = {}): T = run {
    var result: T? = null
    if (isFile && canRead()) {
        val timestr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "")
        val backupFile = parentFile.resolve("$nameWithoutExtension-$timestr.$extension")
        val jsonSerializer = if (strict) jfxJsonSerializer else jfxJsonSerializerNonstrict

        try {
            result = jsonSerializer.parse(serializer, readText())
        } catch (e: Throwable) {
            val success = renameTo(backupFile)
            log.warning("Could not read $name...")

            val backupmessage =
                    if (success) "Ein Backup der alten Einstellungsdatei wurde abgelegt unter\n '${backupFile.absolutePath}'"
                    else "Ein Backup der alten Einstellungsdatei konnte nicht angelegt werden."

            runLater {
                confirm("Could not load $name",
                        """$name konnten nicht geladen werden.
                                    |Neue $name werden angelegt.
                                    |$backupmessage'
                                    |
                                    |Show in file browser?
                                """.trimMargin()) {
                    backupFile.openInExplorer()
                }
            }
            log.throwing(javaClass.simpleName, "loadSettings", e)
        }
    }
    // if the result was loaded properly, apply callback and return, otherwise invoke tefault method.
    return result?.apply(callback) ?: default.invoke()
}


fun Property<File>.absolutePath() = SimpleStringProperty(value?.absolutePath).apply {
    bindBidirectional(this@absolutePath, FileProperty.converter)
}

typealias ReplacementText = Pair<IntRange, String>
typealias PatternReplaceText = Pair<String, String>
typealias InsertText = Pair<Int, String>

val InsertText.asReplacement: ReplacementText get() = first..first to second
//infix fun <A, B> B.at(that: A): Pair<A, B> = Pair(that, this)

inline fun replaceText(read: () -> String, write: (String) -> Unit, vararg replacements: ReplacementText) {
    replacements
            .sortedByDescending {
                it.first.endInclusive
            }
            //.also {
            //    Logger.getLogger("").info(it.map { "${it.first}: ${it.second}" }.joinToString("\n"))
            //}
            .fold(read()) { acc, repl ->
                acc.replaceRange(repl.first, repl.second)
            }.also(write)
}

fun Property<String>.replaceText(vararg replacements: ReplacementText) =
        replaceText({ value }, { value = it }, *replacements)

fun Property<String>.insertText(vararg insertTexts: InsertText) =
        replaceText(*insertTexts.map { it.asReplacement }.toTypedArray())

fun File.replaceText(vararg replacements: ReplacementText) =
        replaceText({ readText() }, { writeText(it) }, *replacements)

fun File.insertText(vararg insertTexts: InsertText) =
        replaceText(*insertTexts.map { it.asReplacement }.toTypedArray())

fun String.replacePatternText(vararg replacements: PatternReplaceText) = replacements.fold(this) { text, repl ->
    text.replace("{{#${repl.first}}}", repl.second)
}

fun InputStream.renderToString(vararg replacements: PatternReplaceText, charset: Charset = Charset.defaultCharset()) = readBytes()
        .toString(charset)
        .replacePatternText(*replacements)

fun InputStream.renderToFile(vararg replacements: PatternReplaceText, file: File, charset: Charset = Charset.defaultCharset()) =
        renderToString(*replacements, charset = charset)
                .let { file.writeText(it, charset) }


fun File.camelCaseId(drops: Int = 1) = this.nameWithoutExtension.split("-", "_").asSequence().drop(drops).camelCase

fun InputStream.copyTo(path: String) = copyTo(path.toFile)
fun InputStream.copyTo(file: File) = file.outputStream().use { copyTo(it) }

fun File.replaceContent(regex: Regex, groupId: String, replacer: (String) -> String) {
    readText().replaceSubgroup(regex, groupId, replacer).let { writeText(it) }
}

fun FileProperty.creatableChildFile(path: String, createFile: Boolean = false) = CreatableFileProperty(this, path, createFile)

class CreatableFileProperty(val parentFile: FileProperty, val path: String, val createFile: Boolean = false) : ReadOnlyProperty<Any, File> {
    override fun getValue(thisRef: Any, property: KProperty<*>): File {
        return parentFile.value.resolve(path).apply {
            parentFile.apply {
                if (!isDirectory) mkdirs()
            }
            if (!exists() && createFile) {
                createNewFile()
            }
        }
    }
}


fun InputStream.bufferedCopyTo(outStream: OutputStream, bufferSize: Int = 4096, cbInterval: Int = 10, callback: (Long) -> Unit): Long {
    val bis = BufferedInputStream(this)
    val bos = BufferedOutputStream(outStream)
    return bis.copyProgress(bos, bufferSize, cbInterval, callback).also {
        bos.flush()
    }
}

fun InputStream.copyProgress(out: OutputStream, bufferSize: Int = 4096, cbInterval: Int = 10, callback: (Long) -> Unit): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    var i = 0
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
        if (i++ % cbInterval == 0) callback.invoke(bytesCopied)
    }
    callback.invoke(bytesCopied)
    return bytesCopied
}


fun File.replaceExtension(extWithDot: String): File = File(absolutePathWithoutExtension + extWithDot)
val File.absolutePathWithoutExtension get() = absolutePath.substringBeforeLast(".")
fun File.appendSuffix(suffix: String) = File("${absolutePathWithoutExtension}$suffix.$extension")


val labelsExtension = "labels"
val imageExtensions = listOf("jpg", "gif", "png", "svg")
val pptFilter = FileChooser.ExtensionFilter("Powerpoint Slides (*.ppt, *.pptx)", "*.ppt", "*.pptx")
val imageFilter = FileChooser.ExtensionFilter("Image Files", *imageExtensions.map { "*.$it" }.toTypedArray())
val exeFilter = FileChooser.ExtensionFilter("Windows Executable (*.exe)", "*.exe")
val zipFileFilter = FileChooser.ExtensionFilter("Zip Archive (*.zip)", "*.zip")
val labelsFilter = FileChooser.ExtensionFilter("Labels Document (*.$labelsExtension)", "*.$labelsExtension")
val anyFilter = FileChooser.ExtensionFilter("Any File", "*")
