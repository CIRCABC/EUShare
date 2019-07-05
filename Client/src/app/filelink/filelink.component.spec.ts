import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilelinkComponent } from './filelink.component';

describe('FilelinkComponent', () => {
  let component: FilelinkComponent;
  let fixture: ComponentFixture<FilelinkComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilelinkComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilelinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
