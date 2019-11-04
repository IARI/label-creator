package com.julianjarecki.ettiketten.styles

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val backgroundNumber by cssclass()
        val somepadding by cssclass()
    }

    init {
        colorPicker {
            padding = box(0.px)

            colorPickerLabel {
                padding = box(0.px, 4.px)
                borderWidth += box(0.px)
                spacing = 0.px
            }
        }

        backgroundNumber {
            fontSize = 16.px
            textFill = Color.BLACK
            fill = Color.BLACK
            opacity = 0.2
            effect = DropShadow(5.0, Color.GRAY)
        }

        somepadding {
            padding = box(6.px)
        }
    }
}