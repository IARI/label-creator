package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.TextContent
import com.julianjarecki.ettiketten.app.data.TextContentModel
import com.julianjarecki.ettiketten.styles.Styles
import com.julianjarecki.tfxserializer.utils.colorpicker
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.*

class TextContentFragment : ItemFragment<TextContent>() {
    val textContent = TextContentModel().bindTo(this)

    var textField: TextField by singleAssign()
    var textArea: TextArea by singleAssign()

    override val root = vbox {
        hbox {
            textField = textfield(textContent.text) {
                removeWhen(textContent.multiline)
                whenVisible {
                    requestFocus()
                    positionCaret(textArea.caretPosition)
                }

                addEventFilter(KeyEvent.KEY_PRESSED) { ev ->
                    if (ev.isShortcutDown || ev.isShiftDown) {
                        when (ev.code) {
                            KeyCode.ENTER -> {
                                val oldText = textContent.text.value
                                val caretPos = caretPosition
                                textContent.text.value = oldText.replaceRange(
                                    caretPos..(caretPos - 1), "\n"
                                )
                                textArea.positionCaret(caretPos + 1)
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            textArea = textarea(textContent.text) {
                minHeight = 30.0
                removeWhen(!textContent.multiline)
                whenVisible {
                    requestFocus()
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