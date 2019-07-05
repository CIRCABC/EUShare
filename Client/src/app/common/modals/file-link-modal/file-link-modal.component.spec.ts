import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FileLinkModalComponent } from './file-link-modal.component';

describe('FileLinkModalComponent', () => {
  let component: FileLinkModalComponent;
  let fixture: ComponentFixture<FileLinkModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FileLinkModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FileLinkModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
