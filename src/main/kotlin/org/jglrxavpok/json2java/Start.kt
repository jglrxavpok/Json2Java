package org.jglrxavpok.json2java

import com.squareup.javapoet.JavaFile
import org.jglrxavpok.json2java.types.ObjectElement
import java.io.FileNotFoundException
import java.io.FileWriter
import java.nio.file.*
import java.util.*

object Start {

    fun start(input: String,
              targetFolder: String,
              groupInFolder: Boolean,
              recurse: Boolean,
              packageName: String,
              mapsFile: String,
              selfReferencingFile: String,
              foldersToFlatten: List<String>
    ) {

        if(!Files.exists(Paths.get(input))) {
            throw FileNotFoundException("Input '$input' does not exist!")
        }

        val baseFolder = Paths.get(targetFolder + "/" + packageName.replace(".", "/"))

        if(!Files.exists(baseFolder)) {
            println("Creating output folder")
            Files.createDirectories(baseFolder)
        }

        val maps = extractPathMatchers(mapsFile)
        val selfReferencing = extractPathMatchers(selfReferencingFile)

        val inputPath = Paths.get(input)
        val inputFolder = if(Files.isDirectory(inputPath)) inputPath else { inputPath.parent ?: Paths.get(".") }
        val fileWalker = FileWalker(inputFolder, baseFolder, maps, selfReferencing)
        Files.walkFileTree(inputPath, fileWalker)

        // TODO: Multithreading
        val convertedClasses = fileWalker.converters.mapValues {
            it.value.map {
                it.convert()
            }
        }

        val output = hashMapOf<Path, ObjectElement>()

        for((folder, elements) in convertedClasses) {
            if(groupInFolder) {
                val merged = elements.reduce { acc, o -> acc.merge(o) }
                merged.rename(folder.substringAfterLast("/"))
                output += baseFolder.resolve(folder) to merged
            } else {
                for(elem in elements) {
                    output += baseFolder.resolve(folder) to elem
                }
            }
        }

        if(groupInFolder && foldersToFlatten.isNotEmpty()) {
            for(toFlatten in foldersToFlatten) {
                val fullPath = baseFolder.resolve(toFlatten)
                val toMerge = LinkedList<ObjectElement>()
                val toRemove = LinkedList<Path>() // list of path keys to remove already generated from "output"
                for((p, o) in output) {
                    if(p.startsWith(fullPath)) {
                        toMerge += o
                        toRemove.add(p)
                    }
                }

                toRemove.forEach { output.remove(it) }

                if(toMerge.isNotEmpty()) {
                    val merged = toMerge.reduce { acc, o -> acc.merge(o) }
                    merged.rename(toFlatten.trimEnd('/').substringAfterLast("/"))
                    output += baseFolder.resolve(toFlatten) to merged
                }
            }
        }

        for((p, o) in output) {
            writeObject(baseFolder, p, o, packageName)
        }

    }

    private fun extractPathMatchers(file: String): List<Pair<Regex, Regex>> {
        val filePath = Paths.get(file)
        val matchers = mutableListOf<Pair<Regex, Regex>>()
        if(Files.exists(filePath)) {
            println("Loading map list $filePath")
            val mapLines = Files.readAllLines(filePath)
            for(line in mapLines) {
                if(';' in line) {
                    val parts = line.split(";")
                    val file = Regex(GlobLike.toRegex(parts[0].trimEnd('/')))
                    val property = Regex(GlobLike.toRegex(parts[1].trimEnd('/')))
                    matchers += file to property
                }
            }
        } else if(file.isNotBlank()) {
            System.err.println("Map file $file was not found!")
        }
        return matchers
    }

    private fun writeObject(baseFolder: Path, targetFolder: Path, elem: ObjectElement, basePackage: String = "") {
        val source = elem.toJavaSource(elem.name)
        var packageName = baseFolder.relativize(targetFolder).toString().replace("\\", "/").replace("/", ".")
        if(basePackage.isNotBlank()) {
            packageName = "$basePackage.$packageName"
        }
        val javaFile = JavaFile.builder(packageName, source).build()
        FileWriter(targetFolder.resolve("${elem.name}.java").toFile()).use {
            it.write(javaFile.toString())
        }
    }

}