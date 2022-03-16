/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { TestBed, inject } from '@angular/core/testing';

import { UploadSuccessGuard } from './upload-success.guard';

describe('UploadSuccessGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UploadSuccessGuard],
    });
  });

  it('should ...', inject([UploadSuccessGuard], (guard: UploadSuccessGuard) => {
    expect(guard).toBeTruthy();
  }));
});
