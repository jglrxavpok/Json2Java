package org.jglrxavpok.json2java.types

interface Element // TODO: code generation methods
data class DoubleElement(val value: Double): Element
data class StringElement(val value: String): Element
data class NumberElement(val value: Number): Element
data class BooleanElement(val value: Boolean): Element
class NullElement: Element
data class ArrayElement(val values: Array<Element>): Element {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayElement

        if (!values.contentEquals(other.values)) return false

        return true
    }
    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}

class ObjectElement: Element {
    val properties = mutableMapOf<String, Element>()

    override fun toString(): String {
        return "Object{$properties}"
    }

    /**
     * Merges both ObjectElements. If both objects have the same property but with different type, it will be converted to an AlternativeElement
     * Properties that are not in both objects will be converted to OptionalElement
     */
    fun merge(other: ObjectElement): ObjectElement {
        val allProperties = (properties.keys + other.properties.keys).distinct()
        val newObject = ObjectElement()
        for(propertyKey in allProperties) {
            // TODO
        }

        return newObject
    }
}
