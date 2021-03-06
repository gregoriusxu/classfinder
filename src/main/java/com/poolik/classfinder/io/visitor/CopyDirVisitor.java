package com.poolik.classfinder.io.visitor;

import com.poolik.classfinder.io.Predicate;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyDirVisitor extends SimpleFileVisitor<Path> {

  private final Path fromPath;
  private final Path toPath;
  private final StandardCopyOption copyOption;
  private final Predicate<Path> copyPredicate;

  public CopyDirVisitor(Path fromPath, Path toPath, Predicate<Path> predicate) {
    this(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, predicate);
  }

  public CopyDirVisitor(Path fromPath, Path toPath, StandardCopyOption copyOption, Predicate<Path> predicate) {
    this.fromPath = fromPath;
    this.toPath = toPath;
    this.copyOption = copyOption;
    this.copyPredicate = predicate;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

    Path targetPath = toPath.resolve(fromPath.relativize(dir));
    if (!Files.exists(targetPath)) {
      Files.createDirectories(targetPath);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    if (copyPredicate.apply(file))
      Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
    return FileVisitResult.CONTINUE;
  }
}