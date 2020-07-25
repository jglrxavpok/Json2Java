package org.jglrxavpok.json2java.types

import com.squareup.javapoet.*
import java.util.*

data class OptionalElement(val element: Element) : Element {

    override fun asType() = when (element) {
        is NumberElement -> {
            when {
                element.isDouble -> TypeName.get(OptionalDouble::class.java)
                element.isLong -> TypeName.get(OptionalLong::class.java)
                else -> TypeName.get(OptionalInt::class.java)
            }
        }
        is BooleanElement -> TypeName.BOOLEAN
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
