package org.jglrxavpok.json2java

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.jglrxavpok.json2java.types.*
import java.io.File
import java.io.FileReader
import java.io.Reader

class JsonStructureExtractor(val reader: Reader) {

    fun convert(): Element {
        val gson = Gson()
        reader.use {
            val obj = gson.fromJson(it, JsonObject::class.java)

            return extract(obj)
        }
    }

    private fun convertJson(elem: JsonElement): Element {
        return when {
            elem.isJsonObject -> extract(elem.asJsonObject)
            elem.isJsonNull -> NullElement()
            elem.isJsonPrimitive -> {
                val prim = elem.asJsonPrimitive
                when {
                    prim.isBoolean -> {
                        BooleanElement(prim.asBoolean)
                    }
                    prim.isString -> {
                        StringElement(prim.asString)
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
                ArrayElement(elem.asJsonArray.map { convertJson(it) }.toTypedArray())
            }
            else -> throw UnsupportedOperationException("Unknown json element type for $elem")
        }
    }

    private fun extract(obj: JsonObject): Element {
        val element = ObjectElement()
        for((k, v) in obj.entrySet()) {
            element.properties[k] = convertJson(v)
        }
        return element
    }
}