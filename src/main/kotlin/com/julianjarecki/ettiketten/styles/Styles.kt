package com.julianjarecki.ettiketten.styles

import javafx.scene.effect.DropShadow
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val labelCellWidth = 180.0
        val labelCellHeight = 130.0
        val labelsTopRowHeight = 60.0
        val labelsLeftBarWidth = 120.0
        val backgroundNumber by cssclass()
        val somepadding by cssclass()
        val labelrepresentation by cssclass()
        val gridBackground by cssclass()
        val noarrow by cssclass()
        val gridLine by cssclass()
        val gridLineList  by cssclass()
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

        gridBackground {
            backgroundColor += Color.WHITE
        }

        labelrepresentation {
            borderWidth += box(1.px)
            borderStyle += BorderStrokeStyle.SOLID
            borderColor += box(Color.LIGHTGRAY)
        }

        gridLine {
            padding = box(0.px)
        }

        gridLineList {
            contains(listCell) {
                padding = box(0.px)

                and(selected) {
                    contains(gridLine) {
                        backgroundColor += Color.LIGHTBLUE
                    }
                }
                //backgroundColor += Color.TRANSPARENT
            }
        }

        noarrow {
            padding = box(0.px)
            borderInsets += box(0.px)
            borderStyle += BorderStrokeStyle.NONE

            child(listCell) {
                padding = box(2.px)
                borderInsets += box(0.px)
                backgroundColor += Color.WHITESMOKE
            }

            child(arrowButton) {
                prefWidth = 0.px
                padding = box(0.px)
                child(arrow) {
                    prefWidth = 0.px
                    padding = box(0.px)
                    opacity = 0.0
                }
            }
        }
    }
}