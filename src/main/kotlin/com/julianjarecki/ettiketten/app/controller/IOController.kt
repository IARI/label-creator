package com.julianjarecki.ettiketten.app.controller

import com.julianjarecki.ettiketten.app.EttikettenApp
import com.julianjarecki.ettiketten.app.data.*
import com.julianjarecki.ettiketten.app.fxproperties.FileProperty
import com.julianjarecki.ettiketten.app.fxproperties.jfxJsonSerializer
import com.julianjarecki.ettiketten.app.utils.*
import com.julianjarecki.ettiketten.view.tabs.DocumentTab
import javafx.scene.image.Image
import tornadofx.*
import java.nio.file.*
import kotlin.reflect.full.createInstance

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
                    syncCreateData()
                }
                save()
            }

            appPreferences.knownDocuments.value.add(ddata)
        }
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
}