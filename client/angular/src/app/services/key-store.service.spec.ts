import { TestBed } from '@angular/core/testing';

import { KeyStoreService } from './key-store.service';

describe('KeyStoreService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: KeyStoreService = TestBed.get(KeyStoreService);
    expect(service).toBeTruthy();
  });
});
