/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MountPoint {

  private Logger log = LoggerFactory.getLogger(MountPoint.class);

  private static final long SIZE_THRESHOLD = 1024L * 1024L * 128L; // keep 128 MiB free

  private final Path root;

  public MountPoint(Path root) throws IOException {
    this.root = root;
    Files.createDirectories(this.root);
  }

  /**
   * Resolves a two way path from {@code id}
   * From {@code id} = 'ARandomID'
   * Resolves {@code root} + '/A/ARandomID'
   */
  private Path getIdPath(String id) {
    if (id == null) {
      throw new NullPointerException("id is null!");
    }

    char firstChar = id.charAt(0);
    return root.resolve(String.valueOf(firstChar)).resolve(id).toAbsolutePath();
  }

  public String getPath() {
    return root.toString();
  }

  /**
   * Returns either 0 or the total space available minus 128Mib
   */
  public long getTotalSpace() {
    return Math.max(root.toFile().getTotalSpace() - SIZE_THRESHOLD, 0);
  }

  public long getUsableSpace() {
    return Math.max(root.toFile().getUsableSpace() - SIZE_THRESHOLD, 0);
  }

  /**
   * Tries to reserve specified space by creating a sparse file.
   * If the file path is already taken, removes it
   * @return Absolute path to file if it could be created, else Optional.empty()
   */
  public Optional<String> tryReserveSpace(String id, long filesize) {
    if (id == null) {
      throw new NullPointerException("id is null!");
    }
    if (this.getUsableSpace() < filesize) {
      return Optional.empty();
    }

    Path path = getIdPath(id).toAbsolutePath();

    try {
      Files.createDirectories(path.getParent());
      try (RandomAccessFile f = new RandomAccessFile(path.toFile(), "rw")) {
        f.setLength(filesize);
      }
    } catch (IOException e) {
      try {
        Files.deleteIfExists(path);
      } catch (IOException ignored) {
        log.warn("Trying to reserve a space already taken !", ignored);
      }

      log.error(String.format("Could not save file: %s", e.toString()), e);

      return Optional.empty();
    }

    return Optional.of(path.toString());
  }
}
