package org.jglrxavpok.json2java

import javax.lang.model.SourceVersion

fun String.escapeKeywords(): String {
    if(SourceVersion.isKeyword(this))
        return "__${this}__"
    return this
}