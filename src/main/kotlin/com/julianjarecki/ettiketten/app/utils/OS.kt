package com.julianjarecki.ettiketten.app.utils

import javafx.scene.image.Image
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

enum class OS {
    Windows, OSX, Linux;

    companion object {
        val current: OS = System.getProperty("os.name")
                .toLowerCase().run {
                    when {
                        startsWith("windows") -> Windows
                        startsWith("linux") -> Linux
                        else -> OSX
                    }
                }
    }
}

fun File.openInExplorer() {
    when (OS.current) {
        OS.Windows -> "explorer.exe /select,${absolutePath}".runCommand(parentFile ?: this)
        OS.OSX -> "open -R ${absolutePath}".runCommand(parentFile ?: this)
        else -> Logger.getGlobal().info("currently not implemented on ${OS.current} (os.name = ${System.getProperty("os.name")})")
    }
}

fun File.openWithDefaultApp() {
    Desktop.getDesktop().open(this)
    /*
        when (OS.current) {
            OS.Windows -> """cmd /c ""start "test" "${absolutePath}""""".runCommand(parentFile)
            else -> Logger.getGlobal().info("currently not implemented on ${OS.current} (os.name = ${System.getProperty("os.name")})")
        }
    */
}

fun Component.snippingTool(): Image? = when (OS.current) {
    OS.Windows -> {
        "snippingtool /clip".runCommand()
        clipboard.image
    }
    else -> null
}

fun String.runProcess(workingDir: File = File("."),
                      timeoutAmount: Long = 60,
                      timeoutUnit: TimeUnit = TimeUnit.MINUTES,
                      vararg vars: Pair<String, String>,
                      op: Process.() -> Unit = {}) =
        ProcessBuilder(*this.split("\\s".toRegex())
                .toTypedArray()).directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .apply {
                    Logger.getGlobal().info("trying to execude")
                    Logger.getGlobal().info(this@runProcess)

                    environment().apply {
                        forEach { Logger.getLogger("").info("${it.key} = ${it.value}") }
                        putAll(vars)
                    }
                }
                .start()
                .apply(op)


fun String.runCommand(workingDir: File = File("."),
                      timeoutAmount: Long = 60,
                      timeoutUnit: TimeUnit = TimeUnit.MINUTES): String? {
    return try {
        this.runProcess(workingDir, timeoutAmount, timeoutUnit) {
            waitFor(timeoutAmount, timeoutUnit)
        }.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}