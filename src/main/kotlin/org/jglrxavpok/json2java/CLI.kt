package org.jglrxavpok.json2java

import kotlinx.cli.*
import java.io.FileNotFoundException
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object CLI {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("json2java")
        val input by parser.option(ArgType.String, shortName = "i", fullName = "input", description = "Input file/folder").required()
        val targetFolder by parser.option(ArgType.String, shortName = "t", fullName = "target", description = "Target output folder").required()
        val groupInFolder by parser.option(ArgType.Boolean, shortName = "g", fullName = "group",
                description = "Consider json files in the same folder as different alternative to the same class. Class name will be name of parent folder.").default(false)
        val recurse by parser.option(ArgType.Boolean, shortName = "r", fullName = "recurse", description = "Recurse in subfolders?").default(true)
        val packageName by parser.option(ArgType.String, shortName = "p", fullName = "package", description = "Name of the base package to generate").default("")

        parser.parse(args)

        if(!Files.exists(Paths.get(input))) {
            throw FileNotFoundException("Input '$input' does not exist!")
        }

        val baseFolder = Paths.get(targetFolder + "/" + packageName.replace(".", "/"))

        if(!Files.exists(baseFolder)) {
            println("Creating output folder")
            Files.createDirectories(baseFolder)
        }

        Files.walkFileTree(Paths.get(input), FileWalker(baseFolder))

        println("Input: $input")
        println("Output: $targetFolder")
    }
}
