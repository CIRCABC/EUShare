/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadFileRowComponent } from './download-file-row.component';

describe('DownloadFileRowComponent', () => {
  let component: DownloadFileRowComponent;
  let fixture: ComponentFixture<DownloadFileRowComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [DownloadFileRowComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadFileRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
