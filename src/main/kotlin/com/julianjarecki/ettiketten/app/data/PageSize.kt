package com.julianjarecki.ettiketten.app.data

import javafx.beans.property.SimpleFloatProperty
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel

@Serializable
class PageSize {
    @ContextualSerialization
    val width = SimpleFloatProperty(595f)
    @ContextualSerialization
    val height = SimpleFloatProperty(842f)
}

enum class PageSizes(val w: Float, val h: Float) {
    A0(2384f, 3370f),
    A1(1684f, 2384f),
    A2(1190f, 1684f),
    A3(842f, 1190f),
    A4(595f, 842f),
    A5(420f, 595f),
    A6(298f, 420f),
    A7(210f, 298f),
    A8(148f, 210f),
    A9(105f, 547f),
    A10(74f, 105f),
    ;

    fun set(ps: PageSize) = ps.apply {
        width.value = w
        height.value = h
    }
}

class PageSizeModel : ItemViewModel<PageSize>() {
    val width = bind(PageSize::width, true)
    val height = bind(PageSize::height, true)
}
