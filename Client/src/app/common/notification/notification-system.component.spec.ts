import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationSystemComponent } from './notification-system.component';

describe('NotificationSystemComponent', () => {
  let component: NotificationSystemComponent;
  let fixture: ComponentFixture<NotificationSystemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NotificationSystemComponent ]
    })
    .compileComponents();
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
