package org.jglrxavpok.json2java.types

import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

interface Element {
    fun generateCode(klass: TypeSpec.Builder, name: String) {
        klass.addField(asType(), name)
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
    override fun asType() = TypeName.DOUBLE // TODO
}

class BooleanElement : Element {
    override fun asType() = TypeName.BOOLEAN
}

class NullElement: Element {
    override fun asType() = TypeName.get(java.lang.Object::class.java)
}

