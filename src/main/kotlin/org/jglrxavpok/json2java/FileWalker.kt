package org.jglrxavpok.json2java

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

class FileWalker(val baseFolder: Path): FileVisitor<Path> {
    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        println(">> $file")
        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
        return FileVisitResult.CONTINUE
    }

    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
        val targetFolder = baseFolder.resolve(dir)
        Files.createDirectories(targetFolder)
        println("Creating output subdirectory: $targetFolder")
        return FileVisitResult.CONTINUE
    }
}