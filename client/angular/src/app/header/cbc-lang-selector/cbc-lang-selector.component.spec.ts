/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CbcLangSelectorComponent } from './cbc-lang-selector.component';

describe('CbcLangSelectorComponent', () => {
  let component: CbcLangSelectorComponent;
  let fixture: ComponentFixture<CbcLangSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CbcLangSelectorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CbcLangSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
