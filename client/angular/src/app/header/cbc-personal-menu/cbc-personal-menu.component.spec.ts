import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CbcPersonalMenuComponent } from './cbc-personal-menu.component';

describe('CbcPersonalMenuComponent', () => {
  let component: CbcPersonalMenuComponent;
  let fixture: ComponentFixture<CbcPersonalMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CbcPersonalMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CbcPersonalMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
