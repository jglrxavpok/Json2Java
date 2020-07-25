package org.jglrxavpok.json2java.types

import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.jglrxavpok.json2java.escapeKeywords
import javax.lang.model.SourceVersion

interface Element {
    fun generateCode(klass: TypeSpec.Builder, name: String) {
        klass.addField(asType(), name.escapeKeywords())
    }

    fun generateAdditional(klass: TypeSpec.Builder, name: String) {}

    fun asType(): TypeName

    fun nameFromType(): String {
        return asType().toString()
    }

    fun mergeWith(unionName: String, other: Element): Element {
        return UnionElement.merge(unionName, this, other)
    }
}

class StringElement: Element {
    override fun asType() = TypeName.get(String::class.java)
}

class NumberElement(val value: Number): Element {
    val isDouble: Boolean
    val isLong: Boolean

    init {
        when {
            value.toDouble() != value.toLong().toDouble() -> {
                isDouble = true
                isLong = false
            }
            value.toLong() != value.toInt().toLong() -> {
                isDouble = false
                isLong = true
            }
            else -> {
                isDouble = false
                isLong = false
            }
        }
    }

    override fun asType(): TypeName {
        return when {
            isDouble -> TypeName.DOUBLE
            isLong -> TypeName.LONG
            else -> TypeName.INT
        }
    }

    override fun mergeWith(unionName: String, other: Element): Element {
        if(other is NumberElement) {
            val resultIsDouble = isDouble || other.isDouble
            val resultIsLong = isLong || other.isLong
            if(resultIsDouble) {
                return NumberElement(0.5)
            }
            if(resultIsLong) {
                return NumberElement(Long.MAX_VALUE)
            }
            return NumberElement(0) // int
        }
        return super.mergeWith(unionName, other)
    }
}

class BooleanElement : Element {
    override fun asType() = TypeName.BOOLEAN
}

class NullElement: Element {
    override fun asType() = TypeName.get(java.lang.Object::class.java)
}

