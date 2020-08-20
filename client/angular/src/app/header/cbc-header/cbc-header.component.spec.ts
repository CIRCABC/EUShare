import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CbcHeaderComponent } from './cbc-header.component';

describe('CbcHeaderComponent', () => {
  let component: CbcHeaderComponent;
  let fixture: ComponentFixture<CbcHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CbcHeaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CbcHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
