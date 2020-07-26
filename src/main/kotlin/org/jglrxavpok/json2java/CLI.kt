package org.jglrxavpok.json2java

import kotlinx.cli.*

object CLI {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("json2java")
        val input: String by parser.option(type = ArgType.String, shortName = "i", fullName = "input", description = "Input file/folder").required()
        val targetFolder: String by parser.option(type = ArgType.String, shortName = "t", fullName = "target", description = "Target output folder").required()
        val groupInFolder: Boolean by parser.option(type = ArgType.Boolean, shortName = "g", fullName = "group",
            description = "Consider json files in the same folder as different alternative to the same class. Class name will be name of parent folder.").default(false)
        val recurse: Boolean by parser.option(type = ArgType.Boolean, shortName = "r", fullName = "recurse", description = "Recurse in subfolders?").default(true)
        val packageName: String by parser.option(type = ArgType.String, shortName = "p", fullName = "package", description = "Name of the base package to generate").default("")
        val maps: String by parser.option(type = ArgType.String, shortName = "m", fullName = "maps",
            description = "Paths of properties which should be interpreted as maps. Format is <path/to/json.json;propA/propB/myMap>. File path can correspond to a folder to apply it to all subfiles").default("")
        val selfReferencing: String by parser.option(type = ArgType.String, shortName = "sf", fullName = "self-refences",
            description = "Paths of properties which should be interpreted as self-refencing. The converter will stop generation of objects with names that already appeared in the file." +
                    "More precisely, it checks that the path contains the same part twice (eg /config/feature/config). " +
                    "Format is <path/to/json.json;propA/propB/myMap>. File path can correspond to a folder to apply it to all subfiles").default("")
        val flatten: List<String> by parser.option(ArgType.String, shortName = "f", fullName = "flatten", description = "Flatten the hierarchy of the given folders. Only used if -g/--group is present").multiple()
        parser.parse(args)
        Start.start(input, targetFolder, groupInFolder, recurse, packageName, maps, selfReferencing, flatten)
    }
}
