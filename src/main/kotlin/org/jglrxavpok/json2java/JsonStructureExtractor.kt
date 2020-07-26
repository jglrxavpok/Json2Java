package org.jglrxavpok.json2java

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.jglrxavpok.json2java.types.*
import java.io.Reader
import java.util.regex.Matcher

class JsonStructureExtractor(val name: String, val reader: Reader, val maps: List<Regex>, val selfReferencing: List<Regex>) {

    fun convert(): ObjectElement {
        val gson = Gson()
        reader.use {
            val obj = gson.fromJson(it, JsonObject::class.java)

            return extract(name, obj, "").apply {
                path = "/"
            }
        }
    }

    private fun convertJson(name: String, elem: JsonElement, path: String): Element {
        return when {
            elem.isJsonObject -> extract(name, elem.asJsonObject, "$path$name")
            elem.isJsonNull -> NullElement()
            elem.isJsonPrimitive -> {
                val prim = elem.asJsonPrimitive
                when {
                    prim.isBoolean -> {
                        BooleanElement()
                    }
                    prim.isString -> {
                        StringElement()
                    }
                    prim.isNumber -> {
                        NumberElement(prim.asNumber)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unknown json element type for $elem")
                    }
                }
            }
            elem.isJsonArray -> {
                ArrayElement(elem.asJsonArray.mapIndexed{ index, it -> convertJson("${name}_Entry", it, "$path$name/_entry_/") }.toTypedArray())
            }
            else -> throw UnsupportedOperationException("Unknown json element type for $elem")
        }
    }

    private fun extract(name: String, obj: JsonObject, path: String): ObjectElement {
        val element = ObjectElement(name)
        for((k, v) in obj.entrySet()) {
            element.properties[k] = convertJson(k, v, "$path/")
        }
        element.path = path
        if(maps.any { it.matches(path) }) {
            element.markAsMap()
        }
        if(selfReferencing.any { it.matches(path) }) {
            element.markAsSelfReferencing()
        }
        return element
    }
}