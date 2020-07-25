package org.jglrxavpok.json2java.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec

class UnionElement(val baseName: String): Element {

    val alternatives = mutableSetOf<Element>()

    companion object {
        fun merge(baseName: String, elementA: Element, elementB: Element): Element {
            val union = when {
                elementA is UnionElement -> {
                    elementA.append(elementB)
                }
                elementB is UnionElement -> elementB.append(elementA)
                else -> UnionElement(baseName).append(elementA).append(elementB)
            }
            if(union.alternatives.size == 1) {
                return union.alternatives.first()
            }
            return union
        }
    }

    private fun append(element: Element): UnionElement {
        if(element is OptionalElement) {
            return append(element.element)
        }
        if(element is UnionElement) {
            for(sub in element.alternatives) {
                append(sub)
            }
            return this
        }
        for(alt in alternatives) {
            if(alt.nameFromType() == element.nameFromType()) {
                return this
            }
        }
        alternatives += element
        return this
    }

    private fun generateUnionClass() = TypeSpec.classBuilder(asType())
        .apply {
            for(alt in alternatives) {
                alt.generateCode(this, "${alt.nameFromType().substringAfterLast(".").decapitalize()}Version")
            }
        }
        .build()

    override fun asType() = ClassName.bestGuess(baseName.capitalize()+"Union")

    override fun generateAdditional(klass: TypeSpec.Builder, name: String) {
        klass.addType(generateUnionClass())
        for(alt in alternatives) {
            alt.generateAdditional(klass, name)
        }
    }
}
