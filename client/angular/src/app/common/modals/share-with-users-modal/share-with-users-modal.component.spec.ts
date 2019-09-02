import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareWithUsersModalComponent } from './share-with-users-modal.component';

describe('ShareWithUsersModalComponent', () => {
  let component: ShareWithUsersModalComponent;
  let fixture: ComponentFixture<ShareWithUsersModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShareWithUsersModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareWithUsersModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
