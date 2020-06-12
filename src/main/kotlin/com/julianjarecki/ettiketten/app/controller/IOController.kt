package com.julianjarecki.ettiketten.app.controller

import com.julianjarecki.ettiketten.app.EttikettenApp
import com.julianjarecki.ettiketten.app.data.*
import com.julianjarecki.ettiketten.app.utils.*
import com.julianjarecki.ettiketten.app.utils.labelsExtension
import com.julianjarecki.ettiketten.view.tabs.DocumentTab
import com.julianjarecki.tfxserializer.app.fxproperties.jfxJsonSerializer
import com.julianjarecki.tfxserializer.fxproperties.FileProperty
import com.julianjarecki.tfxserializer.utils.*
import javafx.scene.image.Image
import tornadofx.*
import java.io.File
import java.nio.file.*

class IOController : Controller() {
    val applicationSettingsPath = FileProperty(
        Paths.get(System.getProperty("user.home"), ".${EttikettenApp.AppName}".camelCase)
    )

    val appPreferencesFile by applicationSettingsPath.creatableChildFile(EttikettenApp.globalSettingsFileName)
    val appPreferences by appSettings()
    private var notificationHandler: (Notification.() -> Unit)? = null

    fun handleNotification(handler: Notification.() -> Unit) {
        notificationHandler = handler
    }

    private fun loadAppData(): AppSettings = appPreferencesFile
        .loadSettings("App Settings", AppSettings.serializer(), log = log)

    fun saveAppPreferences() {
        log.info("Saving global settings...")
        jfxJsonSerializer.stringify(AppSettings.serializer(), appPreferences.item).let {
            appPreferencesFile.writeText(it)
        }
    }

    init {
        applicationSettingsPath.addListener { _ ->
            preferences(EttikettenApp.AppName) {
                put(EttikettenApp.LocalFileStorageKey, applicationSettingsPath.value.absolutePath)
            }
        }

        preferences(EttikettenApp.AppName) {
            get(EttikettenApp.LocalFileStorageKey, null)
                ?.also { applicationSettingsPath.value = it.toFile }
        }

        appPreferences.item = loadAppData()

        subscribe<Notification> {
            notificationHandler?.invoke(it)
        }
    }

    val iconStream get() = resources.stream("/icons/label.png")
    val trayIconStream get() = resources.stream("/icons/label.png")

    val icon by lazy {
        Image(iconStream)
    }

    fun new() {
        inputDialog("New Label", "Create a New Labels Document") { newDocName ->
            val newFile = appPreferences.documentFolder.value.resolve("$newDocName.$labelsExtension")
            newFile.createNewFile()
            //newFile
            val ddata = LabelsDocument().apply {
                labelsFile.value = newFile
                data.value = LabelsDocumentData().apply {
                    units assign appPreferences.defaultUnits
                    columns.add(GridLineData())
                    rows.add(GridLineData())
                    syncCreateData()
                }
                save()
            }

            appPreferences.knownDocuments.value.add(ddata)
        }
    }

    fun copy(doc: LabelsDocument) {
        val oldFile = doc.labelsFile.value
        inputDialog(oldFile.nameWithoutExtension, "Copy ${oldFile.name}") { newDocName ->
            val newFile = appPreferences.documentFolder.value.resolve("$newDocName.$labelsExtension")
            oldFile.copyTo(newFile)

            open(newFile)
        }
    }

    fun rename(doc: LabelsDocument) {
        val oldFile = doc.labelsFile.value
        inputDialog(oldFile.nameWithoutExtension, "rename ${oldFile.name}") { newDocName ->
            val renamedFile = oldFile.parentFile.resolve("$newDocName.$labelsExtension")
            if (!oldFile.renameTo(renamedFile)) {
                warning("Could not rename", "renaming rile '${oldFile.absolutePath}' to ${renamedFile.name} failed.")
            }
            doc.labelsFile.value = renamedFile
        }
    }

    fun open(file: File) = LabelsDocument().apply {
        labelsFile.value = file
        open(this)
    }


    fun open(doc: LabelsDocument) = doc.run {
        val known = appPreferences.knownDocuments.value
        if (known.none { it.labelsFile == doc.labelsFile }) known.add(doc)

        load()
        openNewScope<DocumentTab>(
            this inmodel LabelsDocumentModel::class,
            data.value inmodel LabelsDocumentDataModel::class
        )
    }

    fun delete(doc: LabelsDocument) = doc.run {
        appPreferences.knownDocuments.value.remove(this)
        labelsFile.value.delete()
    }
}