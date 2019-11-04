package com.julianjarecki.ettiketten.view.tabs

/*
import javafx.beans.property.*
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import tornadofx.*

class Test : View("Test") {
    val myData = observableListOf<String>()
    val maxCols = SimpleIntegerProperty(3)

    override val root = vbox {
        hbox {
            button("add") {
                action {
                    myData.add("str-${myData.size}")
                }
            }

            spinner(1, 10, 3, 1, true, maxCols)
        }

        datagrid<String>(myData) {
            maxCellsInRowProperty.bind(maxCols)
        }
    }


    class Item(nullableBoolean: Boolean? = null, nullableString: String? = null) {
        val normalStringProp = SimpleStringProperty( nullableString)
        var nullableString: String? by property(nullableString)
        val nullableStringProperty1: ObjectProperty<String?> = getProperty(Item::nullableString)
        val nullableStringProperty2: SimpleStringProperty = SimpleStringProperty(this, "nullableString")
        var nullableBoolean: Boolean? by property(nullableBoolean)
        val nullableBooleanProperty1: ObjectProperty<Boolean?> = getProperty(Item::nullableBoolean)
        val nullableBooleanProperty2: SimpleBooleanProperty = SimpleBooleanProperty(this, "nullableBoolean")
    }

    class ItemModel : ItemViewModel<Item>(Item()) {
        val normalStringProp = bind<String,SimpleStringProperty,SimpleStringProperty>(Item::normalStringProp)
        val nullableStringProperty1: Property<ObjectProperty<String?>> = bind(Item::nullableStringProperty1)    // instead of Property<String>
        val nullableStringProperty2: Property<String> = bind(Item::nullableStringProperty2)
        //val nullableBooleanProperty1: Property<Boolean?> = bind<Boolean?, ObjectProperty<Boolean?>, ObjectProperty<Boolean?>>(Item::nullableBooleanProperty1) // instead of Property<Boolean>
        //val nullableBooleanProperty1 = bind { item.nullableBooleanProperty1 }
        val nullableBooleanProperty2: Property<Boolean> = bind(Item::nullableBooleanProperty2)
    }

    fun a() {
        Item().apply item@ {
            nullableBooleanProperty1.isNotNull

            //normalString.isNotNull

            ItemModel().apply {
                item = this@item

                normalStringProp.isNotNull
            }
        }
    }
}


class VM2 : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    inline fun <reified PropertyType : Property<T>, reified T : Any?, ResultType : PropertyType> bind2(
        autocommit: Boolean = false,
        forceObjectProperty: Boolean = false,
        defaultValue: T? = null,
        noinline propertyProducer: () -> PropertyType?
    ): ResultType {
        val prop = propertyProducer()

        val facade: Property<*> = if (forceObjectProperty) {
            BindingAwareSimpleObjectProperty<T>(this, prop?.name)
        } else {
            val propertyType = PropertyType::class.java
            val typeParam = T::class.java

            // Match PropertyType against known Property types first
            when {
                IntegerProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleIntegerProperty(
                    this,
                    prop?.name
                )
                LongProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleLongProperty(
                    this,
                    prop?.name
                )
                DoubleProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleDoubleProperty(
                    this,
                    prop?.name
                )
                FloatProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleFloatProperty(
                    this,
                    prop?.name
                )
                BooleanProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleBooleanProperty(
                    this,
                    prop?.name
                )
                StringProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleStringProperty(
                    this,
                    prop?.name
                )
                ObservableList::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleListProperty<T>(
                    this,
                    prop?.name
                )
                SimpleListProperty::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleListProperty<T>(
                    this,
                    prop?.name
                )
                List::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleListProperty<T>(this, prop?.name)
                ObservableSet::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleSetProperty<T>(
                    this,
                    prop?.name
                )
                Set::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleSetProperty<T>(this, prop?.name)
                Map::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleMapProperty<Any, Any>(
                    this,
                    prop?.name
                )
                ObservableMap::class.java.isAssignableFrom(propertyType) -> BindingAwareSimpleMapProperty<Any, Any>(
                    this,
                    prop?.name
                )

                // Match against the type of the Property
                java.lang.Integer::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleIntegerProperty(
                    this,
                    prop?.name
                )
                java.lang.Long::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleLongProperty(
                    this,
                    prop?.name
                )
                java.lang.Double::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleDoubleProperty(
                    this,
                    prop?.name
                )
                java.lang.Float::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleFloatProperty(
                    this,
                    prop?.name
                )
                java.lang.Boolean::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleBooleanProperty(
                    this,
                    prop?.name
                )
                java.lang.String::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleStringProperty(
                    this,
                    prop?.name
                )
                ObservableList::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleListProperty<T>(
                    this,
                    prop?.name
                )
                List::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleListProperty<T>(this, prop?.name)
                ObservableSet::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleSetProperty<T>(
                    this,
                    prop?.name
                )
                Set::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleSetProperty<T>(this, prop?.name)
                Map::class.java.isAssignableFrom(typeParam) -> BindingAwareSimpleMapProperty<Any, Any>(this, prop?.name)

                // Default to Object wrapper
                else -> BindingAwareSimpleObjectProperty<T>(this, prop?.name)
            }
        }

        assignValue(facade, prop, defaultValue)

        facade.addListener(dirtyListener)
        if (facade is ObservableList<*>)
            facade.addListener(dirtyListListener)

        propertyMap[facade] = propertyProducer
        propertyCache[facade] = prop

        // Listener that can track external changes for this facade
        externalChangeListeners[facade] = ChangeListener { _, _, nv ->
            val facadeProperty = (facade as Property<Any>)
            if (!facadeProperty.isBound)
                facadeProperty.value = nv
        }

        // Update facade when the property returned to us is changed externally
        //prop?.addListener(externalChangeListeners[facade]!!)

        // Autocommit makes sure changes are written back to the underlying property. Validation will run before the commit is performed.
        if (autocommit) autocommitProperties.add(facade)

        return facade as ResultType
    }
}
*/
