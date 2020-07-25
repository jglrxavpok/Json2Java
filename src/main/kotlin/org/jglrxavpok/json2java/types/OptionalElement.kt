package org.jglrxavpok.json2java.types

import com.squareup.javapoet.*
import java.util.*

data class OptionalElement(val element: Element) : Element {

    override fun asType() = when (element) {
        is NumberElement -> TypeName.get(OptionalDouble::class.java) // TODO
        is BooleanElement -> TypeName.BOOLEAN // TODO
        is OptionalElement -> element.element.asType()
        else -> ParameterizedTypeName.get(ClassName.get(Optional::class.java), element.asType())
    }

    override fun generateAdditional(klass: TypeSpec.Builder, name: String) {
        element.generateAdditional(klass, name)
    }

    override fun nameFromType(): String = when (element) {
        is OptionalElement -> element.nameFromType()
        else -> element.nameFromType()
    }
}
