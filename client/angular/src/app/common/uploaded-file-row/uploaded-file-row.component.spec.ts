/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadedFileRowComponent } from './uploaded-file-row.component';

describe('FileRowComponent', () => {
  let component: UploadedFileRowComponent;
  let fixture: ComponentFixture<UploadedFileRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UploadedFileRowComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadedFileRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
