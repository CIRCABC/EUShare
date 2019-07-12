import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedWithMeComponent } from './shared-with-me.component';

describe('SharedWithMeComponent', () => {
  let component: SharedWithMeComponent;
  let fixture: ComponentFixture<SharedWithMeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SharedWithMeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedWithMeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
