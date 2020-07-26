package org.jglrxavpok.json2java

import javax.lang.model.SourceVersion

/**
 * Escape Java keywords by prefixing and suffixing with two underscores.
 * Non-keyword strings will be returned directly
 */
fun String.escapeKeywords(): String {
    if(SourceVersion.isKeyword(this))
        return "__${this}__"
    return this
}