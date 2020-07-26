package org.jglrxavpok.json2java.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.jglrxavpok.json2java.escapeKeywords
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier

class ObjectElement(name: String): Element {
    private var forceAsMap: Boolean = false
    private var selfReferencing: Boolean = false
    var path: String = ""
    var name = toCapitalizedCamelCase(name)
        private set
    val properties = mutableMapOf<String, Element>()

    override fun generateCode(klass: TypeSpec.Builder, name: String) {
        if(shouldBeMap()) {
            if(properties.isNotEmpty()) {
                var prop = properties.values.first()
                while(prop is OptionalElement) {
                    prop = prop.element
                }
                val valueType = if(prop is ObjectElement) ClassName.bestGuess("${this.name}Entry") else prop.asType()
                klass.addField(ParameterizedTypeName.get(ClassName.get(Map::class.java), ClassName.get(String::class.java), valueType), name.escapeKeywords())
            }
        } else {
            klass.addField(ClassName.bestGuess(this.name), name.escapeKeywords())
        }
    }

    private fun getValueTypeForMap(): TypeName {
        val values = properties.values.toList()
        // TODO: something cleaner
/*        if(values.map { it.javaClass }.distinct().count() != 1) {
            error("Not allowed to have different element types in an object map! Types are ${values.map { it.javaClass }}")
        }*/
        return values[0].asType()
    }

    private fun shouldBeMap(): Boolean {
        return forceAsMap
    }

    override fun generateAdditional(klass: TypeSpec.Builder, name: String) {
        if(selfReferencing) {
            val partsOfPath = path.split("/")
            if(partsOfPath.distinct().count() != partsOfPath.size) { // loop detected, abort generation
                return
            }
        }
        if(shouldBeMap()) {
            var prop = properties.values.first()
            while(prop is OptionalElement) {
                prop = prop.element
            }
            if(prop is ObjectElement) {
                prop.rename("${this.name}Entry")
            }
            prop.generateAdditional(klass, name)
        } else {
            val nameToUse = nameFromType()
            val subclass = toJavaSource(nameToUse) {
                addModifiers(Modifier.STATIC)
            }
            klass.addType(subclass)
        }
    }

    override fun asType() = ClassName.bestGuess(name)

    override fun toString(): String {
        return "Object{$properties}"
    }

    /**
     * Merges both ObjectElements. If both objects have the same property but with different type, it will be converted to an AlternativeElement
     * Properties that are not in both objects will be converted to OptionalElement
     */
    fun merge(other: ObjectElement): ObjectElement {
        val allProperties = (properties.keys + other.properties.keys).distinct()
        val newObject = ObjectElement(name)
        newObject.path = path ?: other.path
        newObject.forceAsMap = forceAsMap || other.forceAsMap
        newObject.selfReferencing = selfReferencing || other.selfReferencing
        for(propertyKey in allProperties) {
            if(this.properties.containsKey(propertyKey) && other.properties.containsKey(propertyKey)) {
                val propA = this.properties[propertyKey]!!
                val propB = other.properties[propertyKey]!!
                val propertyName = if(SourceVersion.isName(propertyKey)) toCapitalizedCamelCase(propertyKey) else "${this.name}Union"
                newObject.properties[propertyKey] = propA.mergeWith(propertyName, propB)
            } else {
                val originalProperty = if(this.properties.containsKey(propertyKey)) properties[propertyKey] else other.properties[propertyKey]
                if(originalProperty is OptionalElement) {
                    newObject.properties[propertyKey] = originalProperty
                } else {
                    newObject.properties[propertyKey] = OptionalElement(originalProperty!!)
                }
            }
        }

        return newObject
    }

    private fun toCapitalizedCamelCase(str: String) = str.split("_").joinToString("") { it.capitalize() }
    private fun toCamelCase(str: String) = str.split("_").mapIndexed { index, s -> if(index == 0) s else s.capitalize() }.joinToString("")

    fun toJavaSource(nameToUse: String, mod: TypeSpec.Builder.() -> Unit = {}): TypeSpec {
        val klass = TypeSpec.classBuilder(toCapitalizedCamelCase(nameToUse))
        klass.addJavadoc("Path is: $path")
        klass.addModifiers(Modifier.FINAL, Modifier.PUBLIC)
        for(prop in properties) {
            val name = toCamelCase(prop.key)
            prop.value.generateCode(klass, name)
            prop.value.generateAdditional(klass, name)
        }
        klass.mod()
        return klass.build()
    }

    fun rename(newName: String): ObjectElement {
        name = toCapitalizedCamelCase(newName)
        return this
    }

    override fun nameFromType(): String {
        return if(SourceVersion.isName(name)) name else "Entry"
    }

    override fun mergeWith(unionName: String, other: Element): Element {
        if(other is ObjectElement) {
            return merge(other)
        }
        return super.mergeWith(unionName, other)
    }

    fun markAsMap() {
        forceAsMap = true
    }

    fun markAsSelfReferencing() {
        selfReferencing = true
    }
}