package com.julianjarecki.ettiketten.app

import com.julianjarecki.ettiketten.app.controller.IOController
import com.julianjarecki.ettiketten.app.data.AppSettingsModel
import com.julianjarecki.ettiketten.styles.Styles
import com.julianjarecki.ettiketten.view.MainView
import javafx.application.Platform
import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.terminateAsyncExecutors

class EttikettenApp : App(MainView::class, Styles::class) {
    val io: IOController by inject(FX.defaultScope)
    val appSettings by inject<AppSettingsModel>(FX.defaultScope)

    companion object {
        const val globalSettingsFileName = "settings.json"
        const val AppName = "Label Creator"
        const val LocalFileStorageKey = "localFileStoragePath"
    }




    override fun start(stage: Stage) {
        stage.icons.add(io.icon)

        stage.width = appSettings.windowWidth.value
        stage.height = appSettings.windowHeight.value
        stage.isMaximized = appSettings.windowMaximized.value
        stage.x = appSettings.windowX.value
        stage.y = appSettings.windowY.value

        super.start(stage)

        appSettings.windowWidth.bind(stage.widthProperty())
        appSettings.windowHeight.bind(stage.heightProperty())
        appSettings.windowMaximized.bind(stage.maximizedProperty())
        appSettings.windowX.bind(stage.xProperty())
        appSettings.windowY.bind(stage.yProperty())
        //stage.isFullScreen
        //stage.isMaximized = true

        trayicon(io.trayIconStream, implicitExit = true) {
            setOnMouseClicked(fxThread = true) {
                FX.primaryStage.show()
                FX.primaryStage.toFront()
            }

            menu(AppName) {
                item("Show...") {
                    setOnAction(fxThread = true) {
                        FX.primaryStage.show()
                        FX.primaryStage.toFront()
                    }
                }
                item("Exit") {
                    setOnAction(fxThread = true) {
                        Platform.exit()
                    }
                }
            }

            io.handleNotification {
                displayMessage(title, message, type)
            }
        }
    }


    override fun stop() {
        //io.stopWatchService()
        io.saveAppPreferences()

        //Executor.shutdown()
        terminateAsyncExecutors(5000)

        super.stop()
    }
}