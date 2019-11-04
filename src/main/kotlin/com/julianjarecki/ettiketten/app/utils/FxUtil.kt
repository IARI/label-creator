package com.julianjarecki.ettiketten.app.utils

//import com.julianjarecki.ettiketten.app.OpenModelEvent
//import de.imc.content.production.view.modal.MultiChoiceDialog
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Window
import javafx.util.Callback
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import tornadofx.colorpicker as tfxColorpicker

fun <T> ListView<T>.keepselected(op: T.() -> Boolean): Boolean = selectedItem?.run {
    val result = op()
    selectionModel.clearAndSelect(items.indexOf(this))
    result
} ?: false

fun <T> TableView<T>.keepselected(op: T.() -> Boolean): Boolean = selectedItem?.run {
    val result = op()
    selectionModel.clearAndSelect(items.indexOf(this))
    result
} ?: false

fun <T> ListView<T>.moveSelectedUp(): Boolean = keepselected {
    items.moveUp(this)
}

fun <T> TableView<T>.moveSelectedUp(): Boolean = keepselected {
    items.moveUp(this)
}

fun <T> ListView<T>.moveSelectedDown(): Boolean = keepselected {
    items.moveDown(this)
}

fun <T> TableView<T>.moveSelectedDown(): Boolean = keepselected {
    items.moveDown(this)
}

val <T> ListView<T>.selectedIndex get() = items.indexOf(selectedItem).let { if (it < 0) null else it }
val <T> TableView<T>.selectedIndex get() = items.indexOf(selectedItem).let { if (it < 0) null else it }
fun <T> ListView<T>.deleteSelected(): Boolean = selectedItem?.let { items.remove(it) } ?: false
fun <T> TableView<T>.deleteSelected(condition: (T) -> Boolean = { true }): Boolean =
    selectedItem?.let { if (condition(it)) items.remove(it) else false }
        ?: false

fun TabPane.circleNext() {
    if (tabs.isEmpty()) return
    selectionModel.select((selectionModel.selectedIndex + 1) % tabs.size)
}

fun TabPane.circlePrev() {
    if (tabs.isEmpty()) return
    selectionModel.select((selectionModel.selectedIndex - 1) % tabs.size)
}

fun <T> TableView<T>.replaceSelected(newObj: T?): Boolean = selectedItem?.let {
    items.indexOf(it).let { idx ->
        if (idx > 0) {
            items.set(idx, newObj)
            selectionModel.clearAndSelect(idx)
            true
        } else false
    }
} ?: false

fun Region.setSize(w: Double, h: Double) {
    setMinSize(w, h)
    setMaxSize(w, h)
    setPrefSize(w, h)
}

infix fun <T> ObservableValue<T>.hasValue(v: T) = value == v

infix fun <T> Property<T>.assign(v: ObservableValue<out T>?) {
    value = v?.value
}

infix fun <T> ObservableValue<T>.valueEquals(v: ObservableValue<T>) = value == v.value

inline fun <T, reified I : ItemViewModel<T>> I.clone(): I = I::class.createInstance().also {
    it.item = item
}


fun TextField.makeIntfield() {
    textProperty().mutateOnChange {
        it?.toIntOrNull()?.toString()
    }
}

fun TextField.makeDoublefield() {
    textProperty().mutateOnChange {
        it?.toDoubleOrNull()?.toString()
    }
}


/*
inline fun <reified T : OpenModelEvent<*, *>, reified UIC : UIComponent> Component.openOnEvent(crossinline op: (UIC) -> Unit): FXEventRegistration = subscribe<T> { ev ->
    val scope = Scope()
    //projectScope.set(ev.project)
    setInScope(ev.model, scope)
    op(find(UIC::class, scope))
}
*/

fun <T, M : ItemViewModel<T>> Scope.setItem(cls: KClass<M>, v: T) {
    find(cls, this).item = v
}

inline fun <T, reified M : ItemViewModel<T>> Scope.setItem(v: T) {
    find<M>(this).item = v
}

inline fun <T, reified M : ItemViewModel<T>> Scope.fromItemModel(m: M) {
    setItem<T, M>(m.item)
}

val IntRange.indexrange get() = IndexRange(start, end)

fun <S> TableColumn<S, String>.cellFormatEditable(
    formatter: TableCell<S, String>.(String) -> Unit,
    onCommit: TableCell<S, String>.(String) -> Unit = {}
) = cellFormatEditable(DefaultStringConverter(), formatter, onCommit)

fun <S> TableColumn<S, Int>.intCellFormatEditable(
    formatter: TableCell<S, Int>.(Int) -> Unit,
    onCommit: TableCell<S, Int>.(Int) -> Unit = {}
) = cellFormatEditable(IntegerStringConverter(), formatter, onCommit)


fun <S, T> TableColumn<S, T>.cellFormatEditable(
    converter: StringConverter<T>,
    formatter: TableCell<S, T>.(T) -> Unit,
    onCommit: TableCell<S, T>.(T) -> Unit = {}
) {
    cellFactory = Callback {
        object : TextFieldTableCell<S, T>(converter) {
            override fun commitEdit(newValue: T?) {
                super.commitEdit(newValue)
                if (newValue != null) {
                    onCommit.invoke(this, newValue)
                }
            }

            override fun updateItem(item: T?, empty: Boolean) {
                super.updateItem(item, empty)
                //if (empty) {
                //    style = null
                //} else
                if (item == null) {
                    style = null
                    styleClass.clear()
                } else {
                    formatter.invoke(this, item)
                }
            }
        }
    }
}

fun SplitPane.resizableWithParent(node: Node, value: Boolean = true) = SplitPane.setResizableWithParent(node, value)


class BrightnessDelegate<T>(val getColor: (T) -> Paint, val setColor: T.(Paint) -> Unit) :
    ReadWriteProperty<T, Double> {
    override fun getValue(thisRef: T, property: KProperty<*>): Double = getColor(thisRef).let {
        when (it) {
            is Color -> it.brightness
            else -> 0.0
        }
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Double) = getColor(thisRef).let {
        when (it) {
            is Color -> thisRef.setColor(
                if (value < 1) it.deriveColor(.0, 1.0, value, 1.0)
                else it.interpolate(Color.WHITE, Math.min(value, 2.0) - 1)
            )
            else -> {
            }
        }
    }
}

fun <T> KMutableProperty1<T, Paint>.brightness() = BrightnessDelegate<T>(this.getter, this.setter)
var PropertyHolder.CssProperty<Paint>.brightness: Double by PropertyHolder.CssProperty<Paint>::value.brightness()
var CssSelectionBlock.fillBrightness: Double by CssSelectionBlock::fill.brightness()

inline infix fun <reified T : Any> ((T) -> String).stringconverter(crossinline converse: (String) -> T?) =
    object : StringConverter<T>() {
        override fun fromString(string: String?): T = string?.let(converse) ?: T::class.createInstance()

        override fun toString(obj: T?): String = obj?.let(this@stringconverter) ?: ""
    }

inline fun <reified T : Any, B : ObservableValue<out List<T>?>> B.stringconverter(
    notFound: String = "",
    crossinline tostr: (T) -> ObservableValue<String>
) = object : StringConverter<T>() {
    override fun fromString(string: String?): T = value?.firstOrNull { it?.let(tostr)?.value == string }
        ?: T::class.createInstance()

    override fun toString(obj: T?): String = obj?.let(tostr)?.value ?: notFound
}


inline fun <reified T : Any> List<T>.stringconverter(notFound: String = "", crossinline tostr: (T) -> String) =
    object : StringConverter<T>() {
        override fun fromString(string: String?): T = firstOrNull { tostr(it) == string } ?: T::class.createInstance()

        override fun toString(obj: T?): String = obj?.let(tostr) ?: notFound
    }

inline fun <reified T : Any> List<T>.stringconverterProp(
    notFound: String = "",
    crossinline tostr: (T) -> ObservableValue<out String>
): StringConverter<T> {
    val conv: (T) -> String = {
        tostr(it).value
    }
    return stringconverter(notFound, conv)
}

inline fun <T, D : Dialog<T?>> createDialog(
    dialog: D,
    header: String,
    owner: Window? = null,
    title: String? = null,
    actionFn: D.(T) -> Unit = {}
): D {
    title?.let { dialog.title = it }
    dialog.headerText = header
    owner?.also { dialog.initOwner(it) }
    val buttonClicked = dialog.showAndWait()
    if (buttonClicked.isPresent) {
        val result: T? = buttonClicked.get()
        if (result != null) dialog.actionFn(result)
    }
    return dialog
}

inline fun <T> choiceDialog(
    vararg choices: T,
    header: String,
    owner: Window? = null,
    title: String? = null,
    actionFn: ChoiceDialog<T>.(T) -> Unit = {}
): ChoiceDialog<T> {
    val dialog = ChoiceDialog<T>(choices.first(), *choices)
    return createDialog(dialog, header, owner, title, actionFn)
}

/*inline fun <T> mcDialog(vararg choices: Pair<T, BooleanProperty>,
                        header: String,
                        owner: Window? = null,
                        title: String? = null,
                        actionFn: MultiChoiceDialog<T>.(List<T>) -> Unit = {}): MultiChoiceDialog<T> {

    val dialog = MultiChoiceDialog(choices.toList().asObservable())
    return createDialog(dialog, header, owner, title, actionFn)
}

inline fun <T> mcDialog(choices: List<Pair<T, BooleanProperty>>,
                        header: String,
                        owner: Window? = null,
                        title: String? = null,
                        actionFn: MultiChoiceDialog<T>.(List<T>) -> Unit = {}): MultiChoiceDialog<T> {

    val dialog = MultiChoiceDialog(choices.toList().asObservable())
    return createDialog(dialog, header, owner, title, actionFn)
}*/

inline fun inputDialog(
    defaultVal: String?,
    header: String,
    owner: Window? = null,
    title: String? = null,
    actionFn: TextInputDialog.(String) -> Unit = {}
): TextInputDialog {

    val dialog = TextInputDialog(defaultVal)
    return createDialog(dialog, header, owner, title, actionFn)
}

/*inline fun <T> runAsyncProgressDialog(name: String, crossinline op: MultiProgress<T>.() -> Unit) {
    val task = runAsyncComplex<T>(op, {
        information("$name completed")
    }) {
        error("Error", it)
    }
    ProgressDialog(task).apply {
        dialogPane.prefWidth = 640.0
    }.showAndWait()
}*/

inline fun <T, reified N : Any, reified PropertyType : Property<N>, ReturnType : PropertyType> ItemViewModel<T>.bind2(
    property: KProperty1<in T, PropertyType>,
    autocommit: Boolean = false,
    forceObjectProperty: Boolean = false,
    defaultValue: N? = null
): ReturnType = bind(autocommit, forceObjectProperty, defaultValue) { item?.let { property.get(it) } }

val ViewModel.starIfDirty
    get() = dirty.stringBinding {
        if (it == true) " *" else ""
    }

fun ViewModel.starIfDirty(vararg others: ViewModel) = dirty.stringBinding(*others.map { it.dirty }.toTypedArray()) {
    if (it == true || others.any { it.isDirty }) " *" else ""
}

fun EventTarget.colorpicker(
    colorProperty: Property<Color>,
    mode: ColorPickerMode = ColorPickerMode.Button,
    op: ColorPicker.() -> Unit = {}
) = tfxColorpicker(colorProperty as ObjectProperty<Color>, mode, op)


abstract class BidirectionalConversionBinding<S, T> protected constructor(
    protected val propertyS: Property<S>,
    protected val propertyT: Property<T>
) {
    init {
        if (propertyT.isBound) throw IllegalArgumentException("Bound property is not allowed.")
    }

    val updating = SimpleBooleanProperty()
    abstract fun convertStoT(s: S): T
    abstract fun convertTtoS(t: T): S
    val listenerS: ChangeListener<S> by lazy {
        createChangeListenerSetting(propertyT, ::convertStoT)
    }
    val listenerT: ChangeListener<T> by lazy {
        createChangeListenerSetting(propertyS, ::convertTtoS)
    }

    open fun bind() {
        propertyT.value = convertStoT(propertyS.value)
        propertyS.addListener(listenerS)
        propertyT.addListener(listenerT)
    }

    fun triggerUpdate() {
        updating.guarded {
            propertyT.value = convertStoT(propertyS.value)
        }
    }

    protected fun <T1, T2> createChangeListenerSetting(
        changeProp: Property<T2>,
        converter: (T1) -> T2
    ): ChangeListener<T1> =
        ChangeListener { obs: ObservableValue<out T1>?, old: T1, new: T1 ->
            updating.guarded {
                changeProp.value = converter.invoke(new)
            }
        }

    open fun unbind() {
        propertyS.removeListener(listenerS)
        propertyT.removeListener(listenerT)
    }
}


inline fun Property<Boolean>.guarded(op: () -> Unit) {
    synchronized(this) {
        if (!value) {
            value = true
            op.invoke()
            value = false
        }
    }
}

infix fun <T> Property<T>.swapValueWith(other: Property<T>) {
    value.also {
        value = other.value
        other.value = it
    }
}