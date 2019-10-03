import { TestBed } from '@angular/core/testing';

import { UploadedFilesService } from './uploaded-files.service';

describe('UploadedFilesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UploadedFilesService = TestBed.get(UploadedFilesService);
    expect(service).toBeTruthy();
  });
});
