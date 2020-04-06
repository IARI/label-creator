package com.julianjarecki.ettiketten.app.utils

import de.jensd.fx.glyphs.GlyphIcons
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.value.ObservableValue
import javafx.scene.Group
import tornadofx.onChange
import tornadofx.replaceChildren

inline val <T : GlyphIcons> T.view
    get() = when (this) {
        is MaterialDesignIcon -> MaterialDesignIconView(this)
        is FontAwesomeIcon -> FontAwesomeIconView(this)
        is MaterialIcon -> MaterialIconView(this)
        // is EmojiOne -> EmojiOneView(this)
        // is Icons525 -> Icons525View(this)
        // is WeatherIcon -> WeatherIconView(this)
        // is OctIcon -> OctIconView(this)
        else -> throw TypeNotPresentException("", null);
    }


val ObservableValue<GlyphIcons?>.view get() = Group().apply {
    //val content = SimpleOb
    onChange {
        it?.let {
            replaceChildren(it.view)
        }
    }
}