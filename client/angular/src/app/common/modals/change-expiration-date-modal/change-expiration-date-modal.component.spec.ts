/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeExpirationDateModalComponent } from './change-expiration-date-modal.component';

describe('ChangeExpirationDateModalComponent', () => {
  let component: ChangeExpirationDateModalComponent;
  let fixture: ComponentFixture<ChangeExpirationDateModalComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ChangeExpirationDateModalComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeExpirationDateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
