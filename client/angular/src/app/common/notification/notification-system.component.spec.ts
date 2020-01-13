/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationSystemComponent } from './notification-system.component';

describe('NotificationSystemComponent', () => {
  let component: NotificationSystemComponent;
  let fixture: ComponentFixture<NotificationSystemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NotificationSystemComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
