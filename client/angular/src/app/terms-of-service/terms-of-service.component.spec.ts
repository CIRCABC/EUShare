/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TermsOfServiceComponent } from './terms-of-service.component';

describe('TermsOfServiceComponent', () => {
  let component: TermsOfServiceComponent;
  let fixture: ComponentFixture<TermsOfServiceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TermsOfServiceComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TermsOfServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
