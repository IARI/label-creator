package com.julianjarecki.ettiketten.app.controller

import com.julianjarecki.ettiketten.app.data.AppSettingsModel
import tornadofx.*

fun Component.appSettings() = inject<AppSettingsModel>(FX.defaultScope)
fun Component.io() = inject<IOController>(FX.defaultScope)
fun Component.exportPdf() = inject<PdfExportController>(FX.defaultScope)
fun Component.appCtl() = inject<AppController>(FX.defaultScope)