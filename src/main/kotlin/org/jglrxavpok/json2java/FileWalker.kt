package org.jglrxavpok.json2java

import java.io.FileReader
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.ConcurrentHashMap

class FileWalker(val baseInputFolder: Path, val baseFolder: Path, val maps: List<Pair<Regex, Regex>>, val selfReferencing: List<Pair<Regex, Regex>>): FileVisitor<Path> {
    val converters: MutableMap<String, MutableList<JsonStructureExtractor>> = ConcurrentHashMap()

    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        println("Discovered $file")
        val name = file.fileName.toString().substringBeforeLast(".")
        val parentFolderKey = baseInputFolder.relativize(file.parent).toString().replace("\\", "/")
        val list = converters.computeIfAbsent(parentFolderKey) { mutableListOf<JsonStructureExtractor>() }
        val propertyThatAreMaps = mutableListOf<Regex>()
        val propertyThatSelfRefence = mutableListOf<Regex>()
        // if file path match
        for((filePath, jsonPath) in maps) {
            val fileKey = file.toString().replace("\\", "/")
            if(filePath.matches(fileKey)) {
                propertyThatAreMaps += jsonPath
            }
        }

        for((filePath, jsonPath) in selfReferencing) {
            val fileKey = file.toString().replace("\\", "/")
            if(filePath.matches(fileKey)) {
                propertyThatSelfRefence += jsonPath
            }
        }

        list += JsonStructureExtractor(name, FileReader(file.toFile()), propertyThatAreMaps, propertyThatSelfRefence)
        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val targetFolder = baseFolder.resolve(baseInputFolder.relativize(dir))
        Files.createDirectories(targetFolder)
        println("Creating output subdirectory: $targetFolder")
        return FileVisitResult.CONTINUE
    }
}