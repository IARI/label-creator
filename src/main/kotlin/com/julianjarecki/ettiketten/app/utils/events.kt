package com.julianjarecki.ettiketten.app.utils

import com.julianjarecki.ettiketten.view.base.AppTab
import tornadofx.*
import java.awt.TrayIcon
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class Notification(val title: String, val message: String, val type: TrayIcon.MessageType = TrayIcon.MessageType.NONE) : FXEvent()
fun Component.notification(title: String, message: String) = fire(Notification(title, message))
fun Component.notificationInfo(title: String, message: String) = fire(Notification(title, message, TrayIcon.MessageType.INFO))
fun Component.notificationWarn(title: String, message: String) = fire(Notification(title, message, TrayIcon.MessageType.WARNING))
fun Component.notificationError(title: String, message: String) = fire(Notification(title, message, TrayIcon.MessageType.ERROR))

inline fun <reified T : AppTab> Component.openUi(s: Scope = scope) = fire(OpenUiComponent(s, T::class))
inline fun <reified C : AppTab> Component.openNewScope(vararg models: ScopedInstance) = openUi<C>(Scope(*models))
infix inline fun <reified M : ItemViewModel<T>, T> M.newWithitem(it: T) = this::class.createInstance() withitem it
infix inline fun <reified M : ItemViewModel<T>, T> M.withitem(it: T) = apply { item = it }
infix inline fun <reified M : ItemViewModel<T>, T> T.inmodel(m: KClass<M>) = m.createInstance() withitem this

class OpenUiComponent(val tabscope: Scope, val cls: KClass<out AppTab>) : FXEvent()