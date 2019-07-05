import { TestBed } from '@angular/core/testing';

import { ModalsService } from './modals.service';

describe('ModalsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ModalsService = TestBed.get(ModalsService);
    expect(service).toBeTruthy();
  });
});
