package com.julianjarecki.ettiketten.view.fragments

import com.julianjarecki.ettiketten.app.data.TextContent
import com.julianjarecki.ettiketten.app.data.TextContentModel
import com.julianjarecki.ettiketten.app.utils.colorpicker
import com.julianjarecki.ettiketten.app.utils.makeDoublefield
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import tornadofx.*

class TextContentFragment : ItemFragment<TextContent>() {
    val textContent = TextContentModel().bindTo(this)

    override val root = vbox {
        hbox {
            textfield(textContent.text)
            colorpicker(textContent.color, ColorPickerMode.MenuButton) {
                prefWidth = 20.0
            }
        }
        hbox {
            checkbox("auto", textContent.autoSize)
            spacer()
            spinner(5.0, 100.0, amountToStepBy = 0.25, editable =  true, property = textContent.size, enableScroll = true) {
                prefWidth = 64.0
                disableWhen(textContent.autoSize)
            }
            /*
            textfield(textContent.size) {
                disableWhen(textContent.autoSize)
                makeDoublefield()
            }
            */
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