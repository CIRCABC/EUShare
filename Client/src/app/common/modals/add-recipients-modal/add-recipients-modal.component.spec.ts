import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRecipientsModalComponent } from './add-recipients-modal.component';

describe('AddRecipientsModalComponent', () => {
  let component: AddRecipientsModalComponent;
  let fixture: ComponentFixture<AddRecipientsModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddRecipientsModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRecipientsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
