/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { TestBed } from '@angular/core/testing';

import { UploadedFilesService } from './uploaded-files.service';

describe('UploadedFilesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UploadedFilesService = TestBed.get(UploadedFilesService);
    expect(service).toBeTruthy();
  });
});