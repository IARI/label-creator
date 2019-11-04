package com.julianjarecki.ettiketten.app.data

import com.julianjarecki.ettiketten.app.fxproperties.FileProperty
import com.julianjarecki.ettiketten.app.fxproperties.ObjectPropertySerializer
import com.julianjarecki.ettiketten.app.fxproperties.ObservableListSerializer
import com.julianjarecki.ettiketten.app.utils.itext.Units
import com.julianjarecki.ettiketten.app.utils.userHomeDir
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.stage.Screen
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel
import tornadofx.observableListOf

@Serializable
class AppSettings {
    @Serializable(with = ObservableListSerializer::class)
    val knownDocuments = observableListOf<LabelsDocument>()

    val documentFolder = FileProperty(userHomeDir)

    @ContextualSerialization
    val openDocumentAfterExport = SimpleBooleanProperty(true)

    @Serializable(with = ObjectPropertySerializer::class)
    val defaultUnits = SimpleObjectProperty<Units>(Units.Millimeter)

    @ContextualSerialization
    val pdfAuthor = SimpleStringProperty("")


    @ContextualSerialization
    val windowWidth = SimpleDoubleProperty(800.0)
    @ContextualSerialization
    val windowHeight = SimpleDoubleProperty(600.0)
    @ContextualSerialization
    val windowX = SimpleDoubleProperty(100.0)
    @ContextualSerialization
    val windowY = SimpleDoubleProperty(100.0)
    @ContextualSerialization
    val windowMaximized = SimpleBooleanProperty(false)

}

class AppSettingsModel : ItemViewModel<AppSettings>() {
    val knownDocuments = bind(AppSettings::knownDocuments, true)
    val documentFolder = bind(AppSettings::documentFolder, true)
    val openDocumentAfterExport = bind(AppSettings::openDocumentAfterExport, true)
    val defaultUnits = bind<Units, SimpleObjectProperty<Units>, SimpleObjectProperty<Units>>(AppSettings::defaultUnits)
    val pdfAuthor = bind(AppSettings::pdfAuthor, true)
    val windowWidth = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(AppSettings::windowWidth, true)
    val windowHeight = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(AppSettings::windowHeight, true)
    val windowX = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(AppSettings::windowX, true)
    val windowY = bind<Number, SimpleDoubleProperty, SimpleDoubleProperty>(AppSettings::windowY, true)
    val windowMaximized =
        bind<Boolean, SimpleBooleanProperty, SimpleBooleanProperty>(AppSettings::windowMaximized, true)
}