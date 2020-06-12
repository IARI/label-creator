package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.TextContent
import com.julianjarecki.ettiketten.app.data.TextContentModel
import com.julianjarecki.ettiketten.styles.Styles
import com.julianjarecki.tfxserializer.utils.colorpicker
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.*

class TextContentFragment : ItemFragment<TextContent>() {
    val textContent = TextContentModel().bindTo(this)

    override val root = vbox {
        hbox {
            textfield(textContent.text) {
                removeWhen(textContent.multiline)
                whenVisible {
                    requestFocus()
                }

                this.addEventFilter(KeyEvent.KEY_PRESSED) { ev ->
                    if (ev.isShortcutDown || ev.isShiftDown) {
                        when (ev.code) {
                            KeyCode.ENTER -> {
                                textContent.text.value += "\n"
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            textarea(textContent.text) {
                minHeight = 30.0
                removeWhen(!textContent.multiline)
                whenVisible {
                    requestFocus()
                    positionCaret(textContent.text.value.length)
                }
            }
            colorpicker(textContent.color, ColorPickerMode.MenuButton) {
                prefWidth = 21.0
            }
        }
        hbox {
            //checkbox("auto", textContent.autoSize)
            togglebutton("auto") {
                addClass(Styles.miniButton)
                selectedProperty().bindBidirectional(textContent.autoSize)
            }
            spacer()
            spinner(
                5.0, 100.0, amountToStepBy = 0.25, editable = true,
                property = textContent.size, enableScroll = true
            ) {
                prefWidth = 64.0
                disableWhen(textContent.autoSize)
            }

        }
    }
}

fun EventTarget.textContentFragment(
    content: ObservableValue<out TextContent>,
    op: TextContentFragment.() -> Unit = {}
) {
    this += TextContentFragment().apply {
        itemProperty.bind(content)
    }.apply(op)
}