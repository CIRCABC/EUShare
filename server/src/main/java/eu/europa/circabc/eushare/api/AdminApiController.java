/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.api;

import eu.europa.circabc.eushare.model.MountPointSpace;
import eu.europa.circabc.eushare.model.Stat;
import eu.europa.circabc.eushare.services.AdminService;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.storage.entity.MountPoint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@javax.annotation.Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen"
)
@Controller
@RequestMapping("${openapi.cIRCABCShare.base-path:}")
public class AdminApiController implements AdminApi {

  private final NativeWebRequest request;

  @Autowired
  public FileService fileService;

  @Autowired
  public AdminService adminService;


  @org.springframework.beans.factory.annotation.Autowired
  public AdminApiController(NativeWebRequest request) {
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<List<MountPointSpace>> getDiskSpace() {
    // TODO Auto-generated method stub
    List<MountPoint> mountPoints = fileService.getMountPoints();
    List<MountPointSpace> mountPointSpaces = new ArrayList<MountPointSpace>();
    for (MountPoint mountPoint : mountPoints) {
      MountPointSpace mountPointSpace = new MountPointSpace();
      mountPointSpace.setPath(mountPoint.getPath());
      mountPointSpace.setTotalSpace(new BigDecimal(mountPoint.getTotalSpace()));
      mountPointSpace.setUsableSpace(
        new BigDecimal(mountPoint.getUsableSpace())
      );
      mountPointSpaces.add(mountPointSpace);
    }
    return new ResponseEntity<>(mountPointSpaces, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<Stat>> getStats(BigDecimal year) {
    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<List<Stat>>(
      adminService.getStats(Integer.valueOf(year.intValue())),
      responseHeaders,
      HttpStatus.OK
    );
    
  }


  
}
