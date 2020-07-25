package org.jglrxavpok.json2java.types

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

data class ArrayElement(val values: Array<Element>): Element {

    private val elementType: TypeName

    init {
        elementType = if(!isEmpty) {
            if(values.map { it.javaClass }.distinct().count() != 1) {
                error("Not allowed to have different element types in an array!")
            }
            values[0].asType()
        } else {
            TypeName.get(Object::class.java)
        }
    }

    val isEmpty get()= values.isEmpty()

    override fun asType() = ArrayTypeName.of(elementType)

    override fun generateCode(klass: TypeSpec.Builder, name: String) {
        klass.addField(asType(), name)
    }

    override fun generateAdditional(klass: TypeSpec.Builder, name: String) {
        if(!isEmpty) {
            if(values[0] is ObjectElement) {
                val merged = values.map { it as ObjectElement }.reduce { acc, objectElement -> acc.merge(objectElement) }
                merged.generateAdditional(klass, name)
            } else {
                values[0].generateAdditional(klass, name)
            }
        }
    }

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

    override fun nameFromType(): String = when {
        isEmpty -> "EmptyArray"
        else -> values[0].nameFromType()+"s"
    }

    override fun mergeWith(unionName: String, other: Element): Element {
        if(other is ArrayElement) {
            if(isEmpty)
                return other
            return this
        }
        return super.mergeWith(unionName, other)
    }
}