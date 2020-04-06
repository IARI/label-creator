package com.julianjarecki.ettiketten.app.utils

import com.julianjarecki.tfxserializer.utils.toFile
import com.julianjarecki.tfxserializer.utils.userHomeDir
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.util.logging.Logger

@JvmName("folderFieldString")
fun EventTarget.folderfield(
        text: String? = null,
        prop: Property<String>,
        orientation: Orientation = Orientation.HORIZONTAL,
        forceLabelIndent: Boolean = false,
        op: Field.() -> Unit = {}): Field = field(text,
        orientation, forceLabelIndent) {

    label(prop)
    button("select") {
        selectDir(prop, text ?: "")
    }

    apply(op)
}

fun EventTarget.folderfield(
        text: String? = null,
        prop: Property<File>,
        orientation: Orientation = javafx.geometry.Orientation.HORIZONTAL,
        forceLabelIndent: Boolean = false,
        op: Field.() -> Unit = {}): Field = field(text,
        orientation, forceLabelIndent) {

    label(prop)
    // label(selectedImagePathProperty) {
    //     addClass(Styles.somepadding)
    // }
    button("select") {
        selectDir(prop, text ?: "")
    }

    apply(op)
}

fun EventTarget.subFolderField(prop: Property<String>,
                               parent: ObservableValue<String>,
                               filter: (File) -> Boolean = { f -> f.isDirectory }) {
    val theOptions = mutableListOf<String>().asObservable()
    val updateFiles = {
        parent.value.toFile
                .listFiles(filter)
                ?.map { it.name }?.let(theOptions::setAll)
    }
    updateFiles()
    parent.addListener { _ -> updateFiles() }

    combobox(prop, theOptions)
}

@JvmName("selectDirString")
fun Button.selectDir(prop: Property<String>, dirTypName: String = "") {
    action {
        chooseDirectory("Select $dirTypName Directory".replace("  ", " ")) {
            prop.value?.toFile?.letif({ it.isDirectory }) { initialDirectory = it }
        }?.apply {
            prop.value = this.absolutePath
        }
    }
}

fun Property<File>.chooseDirectory(dirTypName: String = "") =
        chooseDirectory("Select $dirTypName Directory".replace("  ", " ")) {
            value?.letif({ it.isDirectory }) { initialDirectory = it }
        }?.apply {
            val old = value.absolutePath
            value = this

            Logger.getGlobal().info("changed from $old to $absolutePath")
        }

fun Button.selectDir(prop: Property<File>, dirTypName: String = "") {
    action {
        prop.chooseDirectory(dirTypName)
    }
}

fun EventTarget.filefield(
        text: String? = null,
        prop: Property<String>,
        filters: Array<FileChooser.ExtensionFilter>,
        mode: FileChooserMode = FileChooserMode.Single,
        orientation: Orientation = javafx.geometry.Orientation.HORIZONTAL,
        forceLabelIndent: Boolean = false,
        defaultDir: File = userHomeDir,
        op: Field.() -> Unit = {}): Field = field(text,
        orientation, forceLabelIndent) {

    label(prop)
//                    label(selectedImagePathProperty) {
//                        addClass(Styles.somepadding)
//                    }
    button("select") {
        selectFile(prop, text ?: "", filters, defaultDir, mode)
    }

    apply(op)

}


fun Button.selectFile(prop: Property<String>, fileTypName: String = "",
                      filters: Array<FileChooser.ExtensionFilter>,
                      defaultDir: File = userHomeDir,
                      mode: FileChooserMode = FileChooserMode.Single) {

    action {
        chooseFile("Select $fileTypName File".replace("  ", " "),
                filters, null, mode) {
            var dir = prop.value.toFile
            while (!dir.isDirectory) {
                dir = dir.parentFile ?: defaultDir
            }
            initialDirectory = dir
        }.letif({ it.isNotEmpty() }) { flist ->
            prop.value = flist.map { it.absolutePath }.joinToString(", ")
        }
    }
}

fun Button.selectFile(prop: Property<File>, fileTypName: String = "",
                      filters: Array<FileChooser.ExtensionFilter>,
                      defaultDir: ()->File = {userHomeDir}) {

    action {
        chooseFile("Select $fileTypName File".replace("  ", " "),
                filters, null, FileChooserMode.Single) {
            var dir = prop.value
            while (!dir.isDirectory) {
                dir = dir.parentFile ?: defaultDir.invoke()
            }
            initialDirectory = dir
        }.letif({ it.isNotEmpty() }) { flist ->
            prop.value = flist.first()
        }
    }
}


fun EventTarget.showProgress(status: TaskStatus, op: HBox.() -> Unit = {}) = hbox {
    progressbar(status.progress) {
        prefWidth = 400.0
    }
    label(status.title) { paddingLeft = 8 }
    visibleWhen { status.running }
    apply(op)
}