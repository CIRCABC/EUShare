/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.utils;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Inspired from https://github.com/lfridael/spring-file-upload-storage/blob/master/core/src/main/java/nl/runnable/spring/fileupload/util/ResourceMultipartFile.java
 */
public class ResourceMultipartFile implements MultipartFile {

  private final Resource resource;

  private final String name;

  private String originalFilename;

  private final String contentType;

  private final int size;

  public ResourceMultipartFile(Resource resource, String name, String contentType, long size) {
    this.resource = resource;
    this.name = name;
    this.contentType = contentType;
    this.size = (int) size;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return resource.exists() && size > 0;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public byte[] getBytes() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(size);
    FileCopyUtils.copy(getInputStream(), buffer);
    return buffer.toByteArray();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return resource.getInputStream();
  }

  @Override
  public void transferTo(File dest) throws IOException {
    FileCopyUtils.copy(getInputStream(), new FileOutputStream(dest));
  }
}